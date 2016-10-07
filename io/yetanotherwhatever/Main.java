package io.yetanotherwhatever;

import sun.misc.BASE64Encoder;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Main {

    static byte[] HmacSHA256(String data, byte[] key) throws Exception  {
        return HmacSHA256(data.getBytes("UTF-8"), key);
    }


    static byte[] HmacSHA256(byte[] data, byte[] key) throws Exception  {
        String algorithm="HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data);
    }

    static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception  {
        byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
        byte[] kDate    = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion  = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(serviceName, kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);
        return kSigning;
    }

    public static void main(String[] args) {

        String aws_secret_key = args[1];
        String algorithm = "AWS4-HMAC-SHA256";
        String dateStamp = "20161006";
        String region = "us-east-1";
        String serviceName = "s3";
        String bucket = "public.yetanotherwhatever.io";
        String accessKeyID = args[0];

        String policy_document = "{\n" +
                "  \"expiration\":\"2018-01-01T00:00:00Z\",\n" +
                "  \"conditions\": [\n" +
                "    {\"bucket\":\"" + bucket + "\"},\n" +
                "    [\"starts-with\",\"$key\",\"uploads/\"],\n" +
                "    {\"acl\":\"private\"},\n" +
                "    {\"success_action_redirect\":\"http://yetanotherwhatever.io/\"},\n" +
                "    {\"x-amz-algorithm\":\"" + algorithm + "\"},\n" +
                "    {\"x-amz-credential\":\"" + accessKeyID + "/" + dateStamp + "/" + region + "/" + serviceName + "/aws4_request\"},\n" +
                "    {\"x-amz-date\":\"" + dateStamp + "T000000Z\"},\n" +
                "    [\"content-length-range\",0,1048576]\n" +
                "  ]\n" +
                "}";

        try {

            policy_document = policy_document.replaceAll("\\s", "");
            String b64_policy_doc = (new BASE64Encoder()).encode(
                    policy_document.getBytes("UTF-8"));


            byte[] signatureKey = getSignatureKey(aws_secret_key, dateStamp, region, serviceName);

            String stringToSign = b64_policy_doc;
            byte[] signingKey = signatureKey;

            byte[] hash = HmacSHA256(stringToSign, signingKey);
            final StringBuilder builder = new StringBuilder();
            for(byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            String signature = builder.toString();


            System.out.println("string to sign");
            System.out.println(stringToSign);
            System.out.println("sig");
            System.out.println(signature);

        }
        catch(Exception e)
        {

        }

    }
}
