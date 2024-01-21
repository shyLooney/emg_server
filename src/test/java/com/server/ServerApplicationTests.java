package com.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.tensorflow.*;
import org.tensorflow.exceptions.TensorFlowException;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.types.TFloat32;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

@SpringBootTest
class ServerApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) throws FileNotFoundException {
        try (SavedModelBundle model = SavedModelBundle.load("model", "serve");
             Session session = model.session()) {
            System.out.println("model " + model);
            System.out.println("session " + session);

            Scanner scanner = new Scanner(new File("signal.txt"));


            float[] array = new float[2000];
            for (int i = 0; i < 2000; i++) {
                array[i] = Float.parseFloat(scanner.nextLine());
            }
            System.out.println(Arrays.toString(array));

            model.graph().operations().forEachRemaining(o -> System.out.println(o.name() + " consumers: " + o.numConsumers()
                    + " numInputs: " + o.numInputs()
                    + " numOutputs: " + o.numOutputs()));

            System.out.println(model);


//			Tensor inputs = Tensor.of(TFloat32.class, TFloat32.vectorOf(array).shape(), 1);
//			Tensor inputs = Tensor.of(TFloat32.class, TFloat32.vectorOf(array).shape());
            TFloat32 vector = TFloat32.vectorOf(array);
            FloatNdArray floatNdArray = NdArrays.ofFloats(vector.shape());
            System.out.println(Shape.of(1).append(vector.shape()));
            System.out.println(floatNdArray);
            System.out.println(vector.shape());
            System.out.println(vector.asRawTensor().data().asFloats().getFloat(1));
            System.out.println("rank = " + vector.rank());

            Tensor inputs = Tensor.of(TFloat32.class, Shape.of(1).append(2000), vector.asRawTensor().data());
//			Tensor inputs = Tensor.of(TFloat32.class, vector.shape().append(1));

            System.out.println(vector.shape().append(1));
            System.out.println(Shape.of(1).append(vector.shape()));
            byte[] bt = new byte[8000];
            vector.copyTo(floatNdArray);
            System.out.println(inputs.asRawTensor().data().read(bt).asFloats().getFloat(1));
            System.out.println(inputs.asRawTensor().data().asFloats().getFloat(1));
            System.out.println(inputs.asRawTensor().numBytes());
            System.out.println(Arrays.toString(bt));
            System.out.println("my vector = " + floatNdArray.getFloat(1));

            Result output = session.runner()
                    .feed("serving_default_reshape_1_input", inputs)
                    .fetch("StatefulPartitionedCall")
                    .run();

            byte[] bytes = new byte[16];

            Tensor val = output.get(0);
            RawTensor rawTensor = val.asRawTensor();
            for (int i = 0; i < rawTensor.data().asFloats().size(); i++) {
                System.out.print("check = " + rawTensor.data().asFloats().getFloat(i) + " ");
            }
            System.out.println();

            for (var item : output) {
                System.out.println(item);
                System.out.println("value = " + item.getValue());
                System.out.println("key = " + item.getKey());
                System.out.println("array = " + item.getValue().asRawTensor().data());
                for (int i = 0; i < item.getValue().asRawTensor().data().asFloats().size(); i++) {
                    System.out.print(item.getValue().asRawTensor().data().asFloats().getFloat(i) + " ");
                }
                System.out.println();
                for (int i = 0; i < 16; i++) {
                    bytes[i] = item.getValue().asRawTensor().data().getByte(i);
                }
            }
        }
        catch (TensorFlowException e) {
            System.out.println(e.getMessage());
        }

    }

}
