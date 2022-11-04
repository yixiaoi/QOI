package cs107;


import java.nio.channels.AsynchronousServerSocketChannel;

/**
 * Utility class to manipulate arrays.
 * @apiNote First Task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @since 1.0
 */
public final class ArrayUtils {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private ArrayUtils(){}

    // ==================================================================================
    // =========================== ARRAY EQUALITY METHODS ===============================
    // ==================================================================================

    /**
     * Check if the content of both arrays is the same
     * @param a1 (byte[]) - First array
     * @param a2 (byte[]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[] a1, byte[] a2){
        if ((a1==null&&a2!=null)|(a2==null&&a1!=null)){
            throw new AssertionError();
        }else {

            if (a1.length==a2.length){
                for (int i=0;i<a1.length;++i){
                    if (a1[i]!=a2[i]){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check if the content of both arrays is the same
     * @param a1 (byte[][]) - First array
     * @param a2 (byte[][]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[][] a1, byte[][] a2){
        if ((a1==null&&a2!=null)|(a2==null&&a1!=null)){
            throw new AssertionError();
        }else {
            if (a1.length==a2.length){
                for (int i=0;i<a1.length;++i){
                    if (a1[i].length==a2[i].length){
                        for (int j=0;j<a1[i].length;++j){
                            if (a1[i][j]!=a2[i][j]){
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    // ==================================================================================
    // ============================ ARRAY WRAPPING METHODS ==============================
    // ==================================================================================

    /**
     * Wrap the given value in an array
     * @param value (byte) - value to wrap
     * @return (byte[]) - array with one element (value)
     */
    public static byte[] wrap(byte value){
        byte wrapValue []={value};
        return wrapValue;
    }

    // ==================================================================================
    // ========================== INTEGER MANIPULATION METHODS ==========================
    // ==================================================================================

    /**
     * Create an Integer using the given array. The input needs to be considered
     * as "Big Endian"
     * (See handout for the definition of "Big Endian")
     * @param bytes (byte[]) - Array of 4 bytes
     * @return (int) - Integer representation of the array
     * @throws AssertionError if the input is null or the input's length is different from 4
     */
    public static int toInt(byte[] bytes) {
        int toInt;
        if (bytes.length==4){
            toInt=bytes[0]<<24|(bytes[1]&0xFF)<<16|(bytes[2]&0xFF)<<8|(bytes[3]&0xFF);
        }else {
            throw new AssertionError();
        }
        return toInt;
    }

    /**
     * Separate the Integer (word) to 4 bytes. The Memory layout of this integer is "Big Endian"
     * (See handout for the definition of "Big Endian")
     * @param value (int) - The integer
     * @return (byte[]) - Big Endian representation of the integer
     */
    public static byte[] fromInt(int value){
        byte[] fromInt =new byte[4];
        fromInt[0]=(byte)(value>>>24);
        fromInt[1]=(byte)(value>>>16);
        fromInt[2]=(byte)(value>>>8);
        fromInt[3]=(byte)(value);
        return fromInt;
    }

    // ==================================================================================
    // ========================== ARRAY CONCATENATION METHODS ===========================
    // ==================================================================================

    /**
     * Concatenate a given sequence of bytes and stores them in an array
     * @param bytes (byte ...) - Sequence of bytes to store in the array
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     */
    public static byte[] concat(byte ... bytes){
        if (bytes==null){
            throw new AssertionError();
        }else {
            return bytes;
        }

    }

    /**
     * Concatenate a given sequence of arrays into one array
     * @param tabs (byte[] ...) - Sequence of arrays
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     * or one of the inner arrays of input is null.
     */
    public static byte[] concat(byte[] ... tabs){
        for (byte[] bytes : tabs) {
            if (bytes == null) {
                throw new AssertionError();
            }
        }
        int length=0;
        for (byte[] bytes:tabs){
            length+=bytes.length;
        }
        int k = 0;
        byte[] seqByte=new byte[length];
        for (int i=0;i<tabs.length;++i){
            for (int j=0;j<tabs[i].length;++j){
                seqByte[k]=tabs[i][j];
                ++k;
            }
        }
        return seqByte;
    }

    // ==================================================================================
    // =========================== ARRAY EXTRACTION METHODS =============================
    // ==================================================================================

    /**
     * Extract an array from another array
     * @param input (byte[]) - Array to extract from
     * @param start (int) - Index in the input array to start the extract from
     * @param length (int) - The number of bytes to extract
     * @return (byte[]) - The extracted array
     * @throws AssertionError if the input is null or start and length are invalid.
     * start + length should also be smaller than the input's length
     */
    public static byte[] extract(byte[] input, int start, int length){
        if (input==null||start>=input.length||start+length>=input.length||start<0||length<1){
         throw new AssertionError();
        }
        byte[] newBytes=new byte[length];
        int k=0;
        for (int i=start;i<start+length;++i){
            newBytes[k]=input[i];
            ++k;
        }
        return newBytes;
    }

    /**
     * Create a partition of the input array.
     * (See handout for more information on how this method works)
     * @param input (byte[]) - The original array
     * @param sizes (int ...) - Sizes of the partitions
     * @return (byte[][]) - Array of input's partitions.
     * The order of the partition is the same as the order in sizes
     * @throws AssertionError if one of the parameters is null
     * or the sum of the elements in sizes is different from the input's length
     */
    public static byte[][] partition(byte[] input, int ... sizes) {
        byte[][] partition = new byte[sizes.length][];
        int k = 0;
        for (int i = 0; i < sizes.length; ++i) {
            byte[] subPartition=new byte[sizes[i]];
            for (int j = 0; j < sizes[i]; ++j) {
                subPartition[j] = input[k];
                ++k;
            }
            partition[i]=subPartition;
        }
        return partition;
    }

    // ==================================================================================
    // ============================== ARRAY FORMATTING METHODS ==========================
    // ==================================================================================

    /**
     * Format a 2-dim integer array
     * where each dimension is a direction in the image to
     * a 2-dim byte array where the first dimension is the pixel
     * and the second dimension is the channel.
     * See handouts for more information on the format.
     * @param input (int[][]) - image data
     * @return (byte [][]) - formatted image data
     * @throws AssertionError if the input is null
     * or one of the inner arrays of input is null
     */
    public static byte[][] imageToChannels(int[][] input){
        if (input==null){
            throw new AssertionError();
        }else {
            int length= input[0].length;
            for (int[] ints : input) {
                if (ints == null||ints.length!=length) {
                    throw new AssertionError();
                }
            }
            int j=0;
            byte[][] channels=new byte[input.length*input[0].length][4];
            for (int[] ints : input) {
                for (int anInt : ints) {
                    channels[j][0] = (byte) (anInt >> 16);
                    channels[j][1] = (byte) (anInt >> 8);
                    channels[j][2] = (byte) anInt;
                    channels[j][3] = (byte) (anInt >> 24);
                    ++j;
                }

            }
            return channels;
        }


    }

    /**
     * Format a 2-dim byte array where the first dimension is the pixel
     * and the second is the channel to a 2-dim int array where the first
     * dimension is the height and the second is the width
     * @param input (byte[][]) : linear representation of the image
     * @param height (int) - Height of the resulting image
     * @param width (int) - Width of the resulting image
     * @return (int[][]) - the image data
     * @throws AssertionError if the input is null
     * or one of the inner arrays of input is null
     * or input's length differs from width * height
     * or height is invalid
     * or width is invalid
     */
    public static int[][] channelsToImage(byte[][] input, int height, int width){
        if (input==null||input.length!=height*width||input[0].length!=4||height<=0||width<=0){
            throw new AssertionError();
        }
        else {
            for (int i=0;i<height*width;++i) {
                if (input[i]==null) {
                    throw new AssertionError();
                }
            }
            int [][] image=new int[height][width];
            int k=0;
            for (int i=0;i<height;++i){
                for (int j=0;j<width;++j){
                    image[i][j]=(input[k][3]<<24|input[k][0]<<16|input[k][1]<<8|input[k][2]);
                    ++k;
                }
            }
            return image;
        }

    }

}