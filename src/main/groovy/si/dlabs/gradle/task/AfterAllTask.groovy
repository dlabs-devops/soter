package si.dlabs.gradle.task

import com.squareup.okhttp.*
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

/**
 * Created by blazsolar on 10/11/14.
 */
class AfterAllTask extends DefaultTask {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final def TRAVIS_JOB_NUMBER = "TRAVIS_JOB_NUMBER"
    private static final def TRAVIS_BUILD_ID = "TRAVIS_BUILD_ID"
    private static final def POLLING_INTERVAL = "LEADER_POLLING_INTERVAL"
    private static final def GITHUB_TOKEN = "GITHUB_TOKEN"

    def buildID
    def pollingInterval
    def ghToken
    def jobNumber
    def isLead
    def thisSuccess

    def success

    private def client

    @TaskAction
    public void afterAll() {

        buildID = System.getenv(TRAVIS_BUILD_ID)
        jobNumber = System.getenv(TRAVIS_JOB_NUMBER)
        ghToken = System.getenv(GITHUB_TOKEN)
        isLead = jobNumber.endsWith(".1") // TODO lead should be job that was started last

        if (!jobNumber) {
            // only one job
            return
        }

        client = getClient()
        pollingInterval = System.getenv(POLLING_INTERVAL) as int;

        def token = getToken();

        waitForOthersToFinish(token)

        finish(token)

    }

    private void waitForOthersToFinish(def token) {

        while (true) {

            def finished = otherFinished(token)
            if (finished) {
                System.out.println("All jobs are finished!")
                break
            }

            sleep(pollingInterval)

        }

    }

    private String getToken() {

        def json = new JsonBuilder()
        json github_token: ghToken

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url("https://api.travis-ci.com/auth/github")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        JsonSlurper slurper = new JsonSlurper();
        def result = slurper.parseText(response.body().string())
        return result.access_token

    }

    private MatrixElement[] matrixSnapshot(def token) {

        Request request = new Request.Builder()
                .url(String.format("https://api.travis-ci.com/builds/%s?access_token=%s", buildID, token))
                .build();

        Response response = client.newCall(request).execute();
        JsonSlurper slurper = new JsonSlurper();
        def result = slurper.parseText(response.body().string())

        MatrixElement[] elements = new MatrixElement[result.matrix.size()];
        for (int i = 0; i < result.matrix.size(); i++) {
            elements[i] = new MatrixElement(result.matrix[i])
        }

        return elements


    }

    private boolean otherFinished(String token) {

        def snapshot = matrixSnapshot(token)
        def finished = true;

        for (def el in snapshot) {
            if (!el.isLeader && !el.isFinished) {
                finished = false;
                break;
            }
        }

        return finished

    }

    private def getClient() {

        // TODO not ok
        final TrustManager[] trustAllCertificates = [
                new X509TrustManager() {

                    @Override
                    void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    X509Certificate[] getAcceptedIssuers() {
                        return null
                    }
                }
//            [
//                    getAcceptedIssuers:{
//                        return null
//                    }
//            ] as X509TrustManager
        ]

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCertificates, new SecureRandom())
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory()

        OkHttpClient client = new OkHttpClient();
        client.setSslSocketFactory(sslSocketFactory)
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        })

        return client;

    }

    private void finish(def token) {

        def snapshot = matrixSnapshot(token)

        boolean success = thisSuccess
        for (s in snapshot) {
            if (!s.isLeader && !s.isSucceeded) {
                success = false
                break
            }
        }

        System.out.println("Success: " + success);
        this.success = success;

    }

    private class MatrixElement {

        def isFinished
        def isSucceeded
        def number
        def isLeader

        MatrixElement(def rawJson) {
            isFinished = rawJson.finished_at != null;
            isSucceeded = rawJson.result == 0
            number = rawJson.number
            isLeader = String.valueOf(rawJson.number).endsWith(".1")
        }
    }

}
