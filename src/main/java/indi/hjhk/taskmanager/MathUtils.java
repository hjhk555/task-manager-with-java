package indi.hjhk.taskmanager;

import javafx.scene.control.TextFormatter;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.function.UnaryOperator;

public class MathUtils {
    public static int parseAndCheckInt(String valName, String strVal, int min, int max) throws AlertMsgException{
        try{
            int intVal = Integer.parseInt(strVal);
            if (intVal < min || intVal > max)
                throw new AlertMsgException(String.format("'%s'越界，应在%d~%d内", valName, min, max));
            return intVal;
        }catch (NumberFormatException e){
            throw new AlertMsgException(String.format("'%s'解析失败，请输入整型数", valName));
        }
    }

    public static UnaryOperator<TextFormatter.Change> integerFilter = new UnaryOperator<TextFormatter.Change>() {
        @Override
        public TextFormatter.Change apply(TextFormatter.Change change) {
            if (change.getControlNewText().matches("-?[0-9]*"))
                return change;
            return null;
        }
    };

    private static byte[] encode = new byte[256];
    private static byte[] decode = new byte[256];

    static {
        int defCode = 23;
        for (int i=0; i<256; i++){
            encode[i] = (byte) (i-128+defCode);
            decode[i] = (byte) (i-128-defCode);
        }
    }

    private static int nextInt(int preInt){
        return (44383 * preInt + 35171) % 49123;
    }

    public static void setCode(int code){
        for (int i=0; i<256; i++) decode[i] = -128;
        code = nextInt(code) % 256;
        int codeZero = (code == 0 ? 1 : code);
        encode[0] = (byte) (codeZero - 128);
        for (int i=1; i<256; i++){
            code = nextInt(code) % 256;
            int iCode = code;
            while (iCode == codeZero || decode[iCode] != -128) iCode = (iCode+1) % 256;
            decode[iCode] = (byte) (i - 128);
            encode[i] = (byte) (iCode - 128);
        }
    }

    public static void writeEncodedString(String str, ObjectOutput out) throws IOException {
        byte[] bytes = str.getBytes();
        out.writeInt(bytes.length);
        for (byte b : bytes){
            out.writeByte(encode[b+128]);
        }
    }

    public static String readEncodeedString(ObjectInput in) throws IOException, ClassNotFoundException {
        int byteLength = in.readInt();
        byte[] bytes = new byte[byteLength];
        for (int i=0; i<byteLength; i++)
            bytes[i] = decode[in.readByte()+128];
        return new String(bytes);
    }
}
