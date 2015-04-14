package com.github.blazsolar.gradle.task

import com.squareup.okhttp.*
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by blazsolar on 10/11/14.
 */
    class AfterAllTask extends DefaultTask {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

    def isLead
    def thisSuccess

    def success

    private def client
    private def pollingInterval

    @TaskAction
    public void afterAll() {

        client = getClient()
        pollingInterval = project.soter.afterAll.pollingInterval

        def token = getToken()

        if(!checkLead(token)) {
            System.out.println("Not lead")
            return
        } else {
            System.out.println("Lead")
        }

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
        json github_token: project.soter.afterAll.ghToken

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

    private boolean checkLead(String token) {

        def matrix = matrixRaw(token)

        def smallest = new Date(0);
        def smallestNumber = null;

        for (def m in matrix) {
            if (!m.started_at) {
                System.out.println("Not yet started: " + m.number)
                return false
            }

            def newDate = dateFormat.parse(m.started_at)

            if (newDate.after(smallest)) {
                smallest = newDate
                smallestNumber = m.number
            }
        }

        return project.soter.afterAll.jobNumber.equals(String.valueOf(smallestNumber))

    }


    private def matrixRaw(def token) {

        Request request = new Request.Builder()
                .url(String.format("https://api.travis-ci.com/builds/%s?access_token=%s", project.soter.afterAll.buildID, token))
                .build();

        Response response = client.newCall(request).execute();
        JsonSlurper slurper = new JsonSlurper();
        def result = slurper.parseText(response.body().string())

        return result.matrix

    }



    private MatrixElement[] matrixSnapshot(def token) {

        def matrix = matrixRaw(token)

        MatrixElement[] elements = new MatrixElement[matrix.size()];
        for (int i = 0; i < matrix.size(); i++) {
            elements[i] = new MatrixElement(matrix[i])
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

        this.success = success;

        Properties properties = new Properties();
        properties.setProperty("success", Boolean.toString(success));

        File propFile = new File("/tmp/ci.properties");
        properties.store(propFile.newWriter(), null);

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
            isLeader = project.soter.afterAll.jobNumber.equals(String.valueOf(rawJson.number))
        }
    }

}
