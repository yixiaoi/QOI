package cs107;

import java.util.ArrayList;

/**
 * "Quite Ok Image" Encoder
 * @apiNote Second task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @since 1.0
 */
public final class QOIEncoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIEncoder(){}

    // ==================================================================================
    // ============================ QUITE OK IMAGE HEADER ===============================
    // ==================================================================================

    /**
     * Generate a "Quite Ok Image" header using the following parameters
     * @param image (Helper.Image) - Image to use
     * @throws AssertionError if the colorspace or the number of channels is corrupted or if the image is null.
     *  (See the "Quite Ok Image" Specification or the handouts of the project for more information)
     * @return (byte[]) - Corresponding "Quite Ok Image" Header
     */
    public static byte[] qoiHeader(Helper.Image image){
        if (image == null || (image.channels() != 3 && image.channels() != 4)
                || (image.color_space() != 0 && image.color_space() != 1)) {
            throw new AssertionError();
        }
        byte[] header = new byte[QOISpecification.HEADER_SIZE];
        header[0] = QOISpecification.QOI_MAGIC[0];
        header[1] = QOISpecification.QOI_MAGIC[1];;
        header[2] = QOISpecification.QOI_MAGIC[2];;
        header[3] = QOISpecification.QOI_MAGIC[3];;

        header[4] = (byte) (image.data()[0].length >>> 24);
        header[5] = (byte) (image.data()[0].length >>> 16);
        header[6] = (byte) (image.data()[0].length >>> 8);
        header[7] = (byte) (image.data()[0].length);

        header[8] = (byte) (image.data().length >>> 24);
        header[9] = (byte) (image.data().length >>> 16);
        header[10] = (byte) (image.data().length >>> 8);
        header[11] = (byte) (image.data().length);

        header[12] = image.channels();
        header[13] = image.color_space();


        return header;
    }

    // ==================================================================================
    // ============================ ATOMIC ENCODING METHODS =============================
    // ==================================================================================

    /**
     * Encode the given pixel using the QOI_OP_RGB schema
     * @param pixel (byte[]) - The Pixel to encode
     * @throws AssertionError if the pixel's length is not 4
     * @return (byte[]) - Encoding of the pixel using the QOI_OP_RGB schema
     */
    public static byte[] qoiOpRGB(byte[] pixel){
        byte[] encode=new byte[4];
        if (pixel.length!=4){
            throw new AssertionError();
        }
        else {
            encode[0]=QOISpecification.QOI_OP_RGB_TAG;
            encode[1]=pixel[0];
            encode[2]=pixel[1];
            encode[3]=pixel[2];
        }
        return encode;
    }

    /**
     * Encode the given pixel using the QOI_OP_RGBA schema
     * @param pixel (byte[]) - The pixel to encode
     * @throws AssertionError if the pixel's length is not 4
     * @return (byte[]) Encoding of the pixel using the QOI_OP_RGBA schema
     */
    public static byte[] qoiOpRGBA(byte[] pixel){

        if (pixel.length!=4){
            throw new AssertionError();
        }
        byte[] encoder = new byte [5];
        encoder [0]=QOISpecification.QOI_OP_RGBA_TAG;
        for (int i=1; i<5;++i){
            encoder [i]= pixel[i-1];}
        return encoder;
    }

    /**
     * Encode the index using the QOI_OP_INDEX schema
     * @param index (byte) - Index of the pixel
     * @throws AssertionError if the index is outside the range of all possible indices
     * @return (byte[]) - Encoding of the index using the QOI_OP_INDEX schema
     */
    public static byte[] qoiOpIndex(byte index){
        if (index>=0&&index<64){
            return new byte[]{(byte) (QOISpecification.QOI_OP_INDEX_TAG|index)};
        }else{
            throw new AssertionError();
        }
    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_DIFF schema
     * @param diff (byte[]) - The difference between 2 pixels
     * @throws AssertionError if diff doesn't respect the constraints or diff's length is not 3
     * (See the handout for the constraints)
     * @return (byte[]) - Encoding of the given difference
     */
    public static byte[] qoiOpDiff(byte[] diff){
        int QOI_OP_DIFF=0x40;
        if (diff.length!=3){
            throw new AssertionError();
        }
        else {
            for (int i=0;i<3;++i){
                if (diff[i]!=-2&&diff[i]!=-1&&diff[i]!=0&&diff[i]!=1){
                    throw new AssertionError();
                }
            }
            return new byte[]{(byte)(QOI_OP_DIFF|diff[0]+2<<4|diff[1]+2<<2|diff[2]+2)};
        }

    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_LUMA schema
     * @param diff (byte[]) - The difference between 2 pixels
     * @throws AssertionError if diff doesn't respect the constraints
     * or diff's length is not 3
     * (See the handout for the constraints)
     * @return (byte[]) - Encoding of the given difference
     */
    public static byte[] qoiOpLuma(byte[] diff){
        if (diff.length!=3||(diff[1]<-32||diff[1]>32)||((diff[0]-diff[1])>8||(diff[0]-diff[1])<-8)||((diff[2]-diff[1])>8||(diff[2]-diff[1])<-8)){
            throw new AssertionError();
        }else {
            int QOI_OP_LUMA=0x80;
            byte[] encode=new byte[2];
            encode[0]=(byte) (QOI_OP_LUMA|diff[1]+32);
            encode[1]=(byte) (((diff[0]-diff[1])+8<<4)|((diff[2]-diff[1])+8));
            return encode;
        }


    }

    /**
     * Encode the number of similar pixels using the QOI_OP_RUN schema
     * @param count (byte) - Number of similar pixels
     * @throws AssertionError if count is not between 0 (exclusive) and 63 (exclusive)
     * @return (byte[]) - Encoding of count
     */
    public static byte[] qoiOpRun(byte count){
        if(count<=0||count>62){
            throw new AssertionError();
        }
        byte encode=(byte) (QOISpecification.QOI_OP_RUN_TAG|count-1);
        return new byte[]{encode};
    }

    // ==================================================================================
    // ============================== GLOBAL ENCODING METHODS  ==========================
    // ==================================================================================

    /**
     * Encode the given image using the "Quite Ok Image" Protocol
     * (See handout for more information about the "Quite Ok Image" protocol)
     * @param image (byte[][]) - Formatted image to encode
     * @return (byte[]) - "Quite Ok Image" representation of the image
     */
    public static byte[] encodeData(byte[][] image){
        byte counter=0;
        int index=0;
        byte[][] encodeData=new byte[image.length*2][0];
        //ArrayList<byte[]> encodeDateAry=new ArrayList<byte[]>();
        byte[][] hachage=new byte[64][4];
        byte[] pixelprecedent=QOISpecification.START_PIXEL;
        for (int i=0;i<image.length;++i){
            //etape 1
            if (ArrayUtils.equals(pixelprecedent,image[i])){
                ++counter;
                if (counter==62||i==image.length-1){
                    encodeData[index++]=qoiOpRun(counter);
                    counter=0;
                }
            }else {
                if(counter>0){
                    encodeData[index++]=qoiOpRun(counter);
                    counter=0;
                }
                //etape 2
                if (ArrayUtils.equals(hachage[QOISpecification.hash(image[i])],image[i])){
                    encodeData[index++]=qoiOpIndex(QOISpecification.hash(image[i]));
                }else {
                    hachage[QOISpecification.hash(image[i])]=image[i];

                    //etape 3
                    if (pixelprecedent[3]==image[i][3]){
                        byte[] diff=new byte[3];
                        boolean diffCon=true;
                        for (int j=0;j<3;++j)
                        {
                            diff[j]=(byte) (image[i][j]-pixelprecedent[j]);
                            if (diff[j]<-2||diff[j]>1){
                                diffCon=false;
                            }
                        }
                        if (diffCon){
                            encodeData[index++]=qoiOpDiff(diff);
                        }// étape 4
                        else if ((diff[1]>=-32&&diff[1]<=31)&&((diff[0]-diff[1])<=7&&(diff[0]-diff[1])>=-8)
                                &&((diff[2]-diff[1])<=7&&(diff[2]-diff[1])>=-8)) {
                            encodeData[index++]=qoiOpLuma(diff);

                        }else {
                            encodeData[index++]=qoiOpRGB(image[i]);
                        }
                    }else {
                        encodeData[index++]=qoiOpRGBA(image[i]);
                    }
                }

            }
            pixelprecedent=image[i];
        }

        return ArrayUtils.concat(encodeData);
    }

    /**
     * Creates the representation in memory of the "Quite Ok Image" file.
     * @apiNote THE FILE IS NOT CREATED YET, THIS IS JUST ITS REPRESENTATION.
     * TO CREATE THE FILE, YOU'LL NEED TO CALL Helper::write
     * @param image (Helper.Image) - Image to encode
     * @return (byte[]) - Binary representation of the "Quite Ok File" of the image
     * @throws AssertionError if the image is null
     */
    public static byte[] qoiFile(Helper.Image image){

        return ArrayUtils.concat(qoiHeader(image),encodeData(ArrayUtils.imageToChannels(image.data())),QOISpecification.QOI_EOF);
    }

}