package com.server.model;

import lombok.extern.slf4j.Slf4j;
import org.tensorflow.*;
import org.tensorflow.exceptions.TensorFlowException;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.op.train.ApplyGradientDescent;
import org.tensorflow.types.TFloat32;

import java.util.Arrays;

//@Component
@Slf4j
public class DefaultNeuronModel implements NeuronModel {
    private SavedModelBundle model;
    private Session session;
    private final int OUTPUT_SIZE = 4;
    private final int INPUT_SIZE = 2000;

    public DefaultNeuronModel() {
        loadModel();
    }


    public void loadModel() {
        try {
            model = SavedModelBundle.load("model", "serve");
        }
        catch (TensorFlowException exception) {
            log.warn(exception.getMessage());
            throw new RuntimeException();
        }
        try {
            session = model.session();
        }
        catch (TensorFlowException exception) {
            log.warn(exception.getMessage());
            model.close();
            throw new RuntimeException();
        }

    }

    public float[] predict(float[] samples) {
        try (TFloat32 vector = TFloat32.vectorOf(samples);
             Tensor inputs = Tensor.of(
                     TFloat32.class,
                     Shape.of(1).append(INPUT_SIZE),
                     vector.asRawTensor().data());
             Result result = session.runner()
                     .feed("serving_default_reshape_1_input", inputs)
                     .fetch("StatefulPartitionedCall")
                     .run();
             Tensor val = result.get(0);
             RawTensor rawTensor = val.asRawTensor();) {

            float[] output = new float[OUTPUT_SIZE];

            for (int i = 0; i < OUTPUT_SIZE; i++) {
                output[i] = rawTensor.data().asFloats().getFloat(i);
            }

            System.out.println("prediction: " + Arrays.toString(output));

            rawTensor.close();
            return output;
        }
    }
}
