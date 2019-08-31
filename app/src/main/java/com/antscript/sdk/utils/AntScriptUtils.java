package com.antscript.sdk.utils;

public class AntScriptUtils {
    static {
        System.loadLibrary("AntModule");
    }

    public static int MAX(int a,int b){
        return (((a) > (b)) ? (a) : (b));
    }
    public static int MIN(int a, int b){
        return (((a) > (b)) ? (b) : (a));
    }

    public static class RGB{
        public int R;
        public int G;
        public int B;
        public RGB(int R,int G,int B){
            this.R=R;
            this.G=G;
            this.B=B;
        }
    }

    public static class DICTINFO{
        public int w;		   //宽度
        public int h;		   //高度
        public int num;	   //点阵个数
        public String name; //名称
        public byte[] data;	//数据
        public DICTINFO(){

        }
    }

    public static native String dmocrGetText(int dictIndex,int w,int h,byte[] data, int lx, int ly, int rx, int ry, int sim);

    public static native int dmocrCreate(String filePath);


    public static RGB Int2Rgb(int rgb)
    {
        int R = rgb > 0xFFFF ? rgb / 0x10000 : 0;
        int G = rgb > 0xFF ? (rgb / 0x100) % 0x100 : 0;
        int B = rgb > 0 ? rgb % 0x100 : 0;
        RGB result = new RGB(R, G, B);
        return result;
    }



}
