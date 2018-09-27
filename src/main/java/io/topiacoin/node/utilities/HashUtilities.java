package io.topiacoin.node.utilities;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HashUtilities {

    /**
     * Verifies that the given data matches the provided hash string.  This method will decode the hash string, create a
     * MessageDigest using the hash's algorithm, run the data through the digest, and compare the resulting hash with
     * the one in the hash string.
     *
     * @param dataHash The encoded hash string to be compared against the data.
     * @param data     The data that is being verified against the hash.
     *
     * @return True if the data matches the hash.  False if the data does not match the hash.
     *
     * @throws NoSuchAlgorithmException If the algorithm specified in the encoded dataHash string is not available on
     *                                  this system.
     */
    public static boolean verifyHash(String dataHash, byte[] data) throws NoSuchAlgorithmException {
        try {
            return verifyHash(dataHash, new ByteArrayInputStream(data));
        } catch ( IOException e ) {
            // NOOP - This should not happen with a ByteArrayInputStream
            return false;
        }
    }

    /**
     * Verifies that the data in the given data stream matches the provided hash string.  This method will decode the
     * hash string, create a MessageDigest using the hash's algorithm, run the data stream through the digest, and
     * compare the resulting hash with the one in the hash string.
     *
     * @param dataHash   The encoded hash string to be compared against the data.
     * @param dataStream The InputStream containing the data that is being verified against the hash.
     *
     * @return True if the data in the data stream matches the hash.  False if the data in the data stream does not
     * match the hash.
     *
     * @throws NoSuchAlgorithmException If the algorithm specified in the encoded dataHash string is not available on
     *                                  this system.
     * @throws IOException If there is an exception reading from the dataStream.
     */
    public static boolean verifyHash(String dataHash, InputStream dataStream) throws NoSuchAlgorithmException, IOException {

        HashInfo hashInfo = new HashInfo(dataHash) ;

        if ( dataStream.markSupported() ) {
            dataStream.mark(Integer.MAX_VALUE);
        }

        // Calculate the Hash of the dataStream
        MessageDigest digest = MessageDigest.getInstance(hashInfo.getAlgorithm());

        DigestInputStream dis = new DigestInputStream(dataStream, digest);
        byte[] buffer = new byte[8192];
        while ( dis.read(buffer) > 0) { }
        dis.close();
        byte[] hash = digest.digest() ;

        boolean matches = Arrays.equals(hashInfo.getHash(), hash);

        if ( dataStream.markSupported() ) {
            dataStream.reset();
        }

        return matches;
    }

    /**
     * Generates an encoded hash string for the given data using the specified algorithm.
     *
     * @param algorithm The hashing algorithm to be used to generate the hash.
     * @param data      The data for which the hash is being generated.
     *
     * @return An encoded hash string containing the algorithm and hash of the input data.
     *
     * @throws NoSuchAlgorithmException If the specified algorithm is not available on this system.
     */
    public static String generateHash(String algorithm, byte[] data) throws NoSuchAlgorithmException {
        try {
            return generateHash(algorithm, new ByteArrayInputStream(data));
        } catch (IOException e){
            // NOOP - This should not happen on a ByteArrayInputStream.
            return null;
        }
    }

    /**
     * Generates an encoded hash string for the given data stream using the specified algorithm.
     *
     * @param algorithm  The hashing algorithm to be used to generate the hash.
     * @param dataStream The data stream for which the hash is being generated.
     *
     * @return An encoded hash string containing the algorithm and hash of the input data stream.
     *
     * @throws NoSuchAlgorithmException If the specified algorithm is not available on this system.
     * @throws IOException If there is an exception reading from the dataStream.
     */
    public static String generateHash(String algorithm, InputStream dataStream) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);

        if ( dataStream.markSupported() ) {
            dataStream.mark(Integer.MAX_VALUE);
        }

        DigestInputStream dis = new DigestInputStream(dataStream, digest);
        byte[] buffer = new byte[8192];
        while ( dis.read(buffer) > 0) { }
        dis.close();
        byte[] hash = digest.digest() ;

        HashInfo hashInfo = new HashInfo(algorithm, hash) ;
        String dataHash = hashInfo.getEncoded();

        if ( dataStream.markSupported()) {
            dataStream.reset();
        }

        return dataHash;
    }


    /**
     * Encapsulates The Hash Information
     */
    public static class HashInfo {
        private String algorithm;
        private byte[] hash;

        /**
         * Constructs a new HashInfo object with the specified algorithm and hash bytes.
         *
         * @param algorithm The algorithm used to generate the hash bytes.
         * @param hash      The hash bytes generated using the algorithm.
         */
        public HashInfo(String algorithm, byte[] hash) {
            this.algorithm = algorithm;
            this.hash = hash;
        }

        /**
         * Constructs a new HashInfo object from the given dataHash string.  The string is decoded to extract the
         * algorithm and raw hash bytes from the encoded string.
         *
         * @param dataHash
         */
        public HashInfo(String dataHash) {

            // Decode the dataHash into the algorithm and raw hash components.
            String[] parts = dataHash.split(":") ;
            String algorithm = parts[0] ;
            String hashString = parts[1];
            byte[] hash = Base64.decodeBase64(hashString) ;

            this.algorithm = algorithm;
            this.hash = hash;
        }

        /**
         * Returns a string form of this hash info containing the encoded algorithm and raw hash.
         *
         * @return A string containing the encoded form of this hash info.
         */
        public String getEncoded() {
            // Encode the algorithm and hash into a standard encoded string.
            StringBuffer sb = new StringBuffer();
            sb.append(algorithm) ;
            sb.append(":") ;
            sb.append(Base64.encodeBase64String(hash));

            return sb.toString();
        }

        /**
         * Returns the name of the algorithm used to generate the associated hash.  The algorithm name is suitable for
         * use with the
         * <code>MessageDigest.getInstance()</code> method.
         *
         * @return The name of the algorithm used to generate the associated hash.
         */
        public String getAlgorithm() {
            return algorithm;
        }

        /**
         * Returns the byte array representing this hash.
         *
         * @return The byte array representing this hash.
         */
        public byte[] getHash() {
            return hash;
        }
    }
}
