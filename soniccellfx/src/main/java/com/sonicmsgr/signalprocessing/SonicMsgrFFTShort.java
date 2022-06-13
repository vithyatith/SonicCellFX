package com.sonicmsgr.signalprocessing;

/**
 * Computes 1D Discrete Fourier Transform (DFT) of complex and real, single
 * precision data. The size of the data can be an arbitrary number. This is a
 * parallel implementation of split-radix and mixed-radix algorithms optimized
 * for SMP systems. <br>
 * <br>
 * This code is derived from General Purpose FFT Package written by Takuya Ooura
 * (http://www.kurims.kyoto-u.ac.jp/~ooura/fft.html) and from JFFTPack written
 * by Baoshe Zhang (http://jfftpack.sourceforge.net/)
 *
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 */
public final class SonicMsgrFFTShort {

    private int nFFTSize;

    private final long nl;

    private int nBluestein;

    private long nBluesteinl;

    private final int[] ip;

    private final float[] w;

    private final int nw;

    private long nwl;

    private final int nc;

    private long ncl;

    private float[] wtable;

    private float[] wtable_r;

    private float[] bk1;

    private float[] bk2;

    //private Plans plan;
    private final boolean useLargeArrays;

    private static final int[] factors = {4, 2, 3, 5};

    private static final float PI = 3.14159265358979311599796346854418516f;

    private static final float TWO_PI = 6.28318530717958623199592693708837032f;

    /**
     * Creates new instance of FloatFFT_1D.
     *
     * @param n size of data
     */
    private int indexStartK = -1;
    private int indexEndK = -1;
    // private int nFFTSize = 0;

    public SonicMsgrFFTShort(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n must be greater than 0");
        }
        this.useLargeArrays = false;
        this.nFFTSize = n;
        this.nl = n;
        nFFTSize = n;

        // plan = Plans.SPLIT_RADIX;
        this.ip = new int[2 + (int) Math.ceil(2 + (1 << (int) (Math.log(n + 0.5f) / Math.log(2)) / 2))];
        this.w = new float[this.nFFTSize];
        int twon = 2 * this.nFFTSize;
        nw = twon >> 2;
        SonicMsgrFFTUtilsShort.makewt(nw, ip, w);
        nc = this.nFFTSize >> 2;
        //  SonicMsgrFFTUtils.makect_(nc, w, nw, ip);

    }

    public int getSize() {
        return this.nFFTSize;
    }

    public void setIndexRange(int startI, int endI) {
        indexStartK = startI;
        indexEndK = endI;
    }

    public void realForward(float[] a) {
        realForward(a, 0);
    }

    public void realForward(float[] a, int offa) {

        if (nFFTSize == 1) {
            return;
        }
        float xi;

        if (nFFTSize > 4) {
            SonicMsgrFFTUtilsShort.cftfsub(nFFTSize, a, offa, ip, nw, w);
            //SonicMsgrFFTUtils.rftfsub(nFFTSize, a, offa, nc, w, nw);
        } else if (nFFTSize == 4) {
            SonicMsgrFFTUtilsShort.cftx020(a, offa);
        }
        xi = a[offa] - a[offa + 1];
        a[offa] += a[offa + 1];
        a[offa + 1] = xi;
    }

    public void realForwardFull(float[] a) {
        realForwardFull(a, 0);
    }

    public void realForwardFull(final float[] a, final int offa) {

        int idx1, idx2;
        final int twon = 2 * nFFTSize;
        int len = nFFTSize / 2;

        if ((indexStartK < 0) || (indexStartK < 0)) {
            indexStartK = 0;
            indexEndK = len;
        }

        realForward(a, offa);
        //for (int k = 0; k < len; k++) {
        for (int k = indexStartK; k < indexEndK; k++) {
            idx1 = 2 * k;
            idx2 = offa + ((twon - idx1) % twon);
            a[idx2] = a[offa + idx1];
            a[idx2 + 1] = -a[offa + idx1 + 1];
        }
        //   }
        a[offa + nFFTSize] = -a[offa + 1];
        a[offa + 1] = 0;

    }

    public static void main(String[] args) {

        int fftSize = 2048;

        SonicMsgrFFTShort fft = new SonicMsgrFFTShort(fftSize);
        //fft.setIndexRange(0, 1);
        float[] data = new float[fftSize * 2];

        for (int i = 0; i < data.length / 2; i++) {
            data[i] = i;
        }
        fft.realForwardFull(data);
        double avg = 0;
        for (int i = 0; i < data.length; i++) {
            double v = data[i];
            avg = avg + v;
            System.out.println(i + " = " + v + "    avg = " + avg);
        }
        avg = avg / data.length;
        System.out.println("avg = " + avg);

    }
}
