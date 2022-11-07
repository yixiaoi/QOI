package cs107;

import java.util.Arrays;

import static cs107.Helper.Image;

/**
 * "Quite Ok Image" Decoder
 * @apiNote Third task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @since 1.0
 */
public final class QOIDecoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIDecoder(){}

    // ==================================================================================
    // =========================== QUITE OK IMAGE HEADER ================================
    // ==================================================================================

    /**
     * Extract useful information from the "Quite Ok Image" header
     * @param header (byte[]) - A "Quite Ok Image" header
     * @return (int[]) - Array such as its content is {width, height, channels, color space}
     * @throws AssertionError See handouts section 6.1
     */
    public static int[] decodeHeader(byte[] header){
        boolean qoiMagic= ArrayUtils.equals(ArrayUtils.extract(header,0,4),QOISpecification.QOI_MAGIC);
        if (header==null||header.length!=QOISpecification.HEADER_SIZE|| !qoiMagic||
                (header[12]!=QOISpecification.RGB&header[12]!=QOISpecification.RGBA||
                        (header[13]!=QOISpecification.sRGB&header[13]!=QOISpecification.ALL))){
            throw new AssertionError();
        }
        int[] decoded=new int[4];
        {
            decoded[0]=header[4]<<24|header[5]<<16|header[6]<<8|header[7];
            decoded[1]=header[8]<<24|header[9]<<16|header[10]<<8|header[11];
            decoded[2]=header[12];
            decoded[3]=header[13];
        }
        return decoded;
    }

    // ==================================================================================
    // =========================== ATOMIC DECODING METHODS ==============================
    // ==================================================================================

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param input (byte[]) - Stream of bytes to read from
     * @param alpha (byte) - Alpha component of the pixel
     * @param position (int) - Index in the buffer
     * @param idx (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.1
     */
    public static int decodeQoiOpRGB(byte[][] buffer, byte[] input, byte alpha, int position, int idx){
        return Helper.fail("Not Implemented");
    }

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param input (byte[]) - Stream of bytes to read from
     * @param position (int) - Index in the buffer
     * @param idx (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.2
     */
    public static int decodeQoiOpRGBA(byte[][] buffer, byte[] input, int position, int idx){
        if (buffer==null||input==null||position<0||position>=buffer.length||idx>=input.length||input.length<5){
            throw new AssertionError();
        }else {
            buffer[position]=ArrayUtils.extract(input,idx,4);
            return 4;
        }

    }

    /**
     * Create a new pixel following the "QOI_OP_DIFF" schema.
     * @param previousPixel (byte[]) - The previous pixel
     * @param chunk (byte) - A "QOI_OP_DIFF" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.4
     */
    public static byte[] decodeQoiOpDiff(byte[] previousPixel, byte chunk){
        byte[] currentPixel=new byte[4];
        if (previousPixel==null||previousPixel.length!=4||(chunk&(byte) 0b11_00_00_00)!=QOISpecification.QOI_OP_DIFF_TAG){
            throw new AssertionError();
        }else {
            currentPixel[0]=(byte) (previousPixel[0]+((chunk&(byte) 0b00_11_00_00)>>4)-2);
            currentPixel[1]=(byte) (previousPixel[1]+((chunk&(byte) 0b00_00_11_00)>>2)-2);
            currentPixel[2]=(byte) (previousPixel[2]+(chunk&(byte) 0b00_00_00_11)-2);
            currentPixel[3]=previousPixel[3];
        }
        return currentPixel;
    }

    /**
     * Create a new pixel following the "QOI_OP_LUMA" schema
     * @param previousPixel (byte[]) - The previous pixel
     * @param data (byte[]) - A "QOI_OP_LUMA" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.5
     */
    public static byte[] decodeQoiOpLuma(byte[] previousPixel, byte[] data){
        byte[] currentPixel=new byte[4];
        if (previousPixel==null||data==null||previousPixel.length!=4||((data[0])&(byte) 0b11_00_00_00)!=(byte)0b10_00_00_00){
            throw new AssertionError();
        }
        else {
            byte dg=(byte) ((data[0]&(byte) 0b00_11_11_11)-(byte) 32);
            currentPixel[0]=(byte) (previousPixel[0]+(data[1]>>4&0b11_11)-8+dg);
            currentPixel[1]=(byte) (previousPixel[1]+dg);
            currentPixel[2]=(byte) (previousPixel[2]+(data[1]&(byte) 0b00_00_11_11)-8+dg);
            currentPixel[3]=previousPixel[3];
        }
        return currentPixel;
    }

    /**
     * Store the given pixel in the buffer multiple times
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param pixel (byte[]) - The pixel to store
     * @param chunk (byte) - a QOI_OP_RUN data chunk
     * @param position (int) - Index in buffer to start writing from
     * @return (int) - number of written pixels in buffer
     * @throws AssertionError See handouts section 6.2.6
     */
    public static int decodeQoiOpRun(byte[][] buffer, byte[] pixel, byte chunk, int position){
        byte counter=(byte) (chunk&(byte)0b00_11_11_11);
        if (buffer==null||position<0||position>=buffer.length||pixel==null||pixel.length!=4||
                buffer.length<(chunk&(byte)0b00_11_11_11)+1){
            throw new AssertionError();
        }else {
            for (int i=position;i<=counter+position;++i){
                buffer[i]=pixel;
            }
        }
        return counter;
    }

    // ==================================================================================
    // ========================= GLOBAL DECODING METHODS ================================
    // ==================================================================================

    /**
     * Decode the given data using the "Quite Ok Image" Protocol
     * @param data (byte[]) - Data to decode
     * @param width (int) - The width of the expected output
     * @param height (int) - The height of the expected output
     * @return (byte[][]) - Decoded "Quite Ok Image"
     * @throws AssertionError See handouts section 6.3
     */
    public static byte[][] decodeData(byte[] data, int width, int height){
        byte[][] decodeData=new byte[width*height][4];
        if (data==null||width<0||height<0||data.length<width*height){
            throw new AssertionError();
        }
        else {
            for (int idx=0;idx< data.length;++idx){
                data
            }

        }
    }

    /**
     * Decode a file using the "Quite Ok Image" Protocol
     * @param content (byte[]) - Content of the file to decode
     * @return (Image) - Decoded image
     * @throws AssertionError if content is null
     */
    public static Image decodeQoiFile(byte[] content){
        return Helper.fail("Not Implemented");
    }

}