package si.dlabs.gradle.task
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
/**
 * Created by blazsolar on 16/09/14.
 */
class UploadTask extends DefaultTask {

    String accessKey;

    String secretKey;

    String bucket;

    File[] files;

    String keyPrefix = ""

    boolean isPublic = false;

    private AmazonS3 mAmazonS3;

    @TaskAction
    public void upload() {

        AWSCredentials pcredentials = new BasicAWSCredentials(accessKey, secretKey);
        mAmazonS3 = new AmazonS3Client(credentials);

        files.each { File file ->
            if (file.isDirectory()) {
                uploadDir("", file)
            } else {
                uploadFile("", file)
            }
        }

    }

    void uploadFile(String path, File file) {
        def putObjectRequest = new PutObjectRequest(
                bucket, keyPrefix + path + file.getName(), file
        )

        if (isPublic) {
            putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead)
        } else {
            putObjectRequest.setCannedAcl(CannedAccessControlList.Private)
        }

        mAmazonS3.putObject(putObjectRequest);
    }

    void uploadDir(String path, File dir) {
        path += dir.getName() + "/";

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                uploadDir(path, file)
            } else {
                uploadFile(path, file)
            }
        }
    }

    public void setFile(File file) {
        files = [ file ];
    }

}
