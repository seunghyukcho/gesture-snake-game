package teamproject.csed402.postech.com.gesturesnakegame.utilities;

public class LinearLayer {
    double[][] weights;
    double[][] bias;
    int w_dim1;
    int w_dim2;
    int b_dim;

    LinearLayer(String weight_s, String bias_s) {
        weights = loadParameters(weight_s, false);
        bias = loadParameters(bias_s, true);
    }

    double[] forward(double[] input) {
        double[] output = new double[w_dim1];

        for (int i = 0; i < w_dim1; i++) {
            double sum = 0;
            for (int j = 0; j < w_dim2; j++) {
                double x = input[j];
                double w = weights[i][j];
                sum += x * w;
            }
            output[i] = sum + bias[0][i];
        }
        return output;
    }

    double[] relu(double[] input) {
        for (int i = 0; i < w_dim1; i++) {
            input[i] = Math.max(0, input[i]);
        }
        return input;
    }

    double[][] loadParameters(String arr, Boolean isBias) {
        String[] items = arr.split(" ");
        int iMax = Integer.parseInt(items[0]);
        int jMax = Integer.parseInt(items[1]);
        double[][] params = new double[iMax][jMax];

        for (int i = 0; i < iMax; i++) {
            for (int j = 0; j < jMax; j++) {

                params[i][j] = Double.parseDouble(items[i * jMax + j + 2]);

            }
        }
        if (isBias) {
            b_dim = jMax;
        } else {
            w_dim1 = iMax;
            w_dim2 = jMax;
        }
        return params;
    }
}