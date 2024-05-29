package com.server.model;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.tensorflow.*;
import org.tensorflow.exceptions.TensorFlowException;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.types.TFloat32;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

//@Component
@Slf4j
public class DefaultNeuralNetwork extends NeuralNetwork {
    private SavedModelBundle model;
    private Session session;
    private final int OUTPUT_SIZE = 4;
    private final int INPUT_SIZE = 2000;
    private NeuralNetworkConfig neuralNetworkConfig;


    public DefaultNeuralNetwork(Path dir) {
        super(dir);

        try {
            ObjectMapper mapper = new ObjectMapper();
            neuralNetworkConfig = mapper.readValue(dir.resolve("config.json").toFile(), NeuralNetworkConfig.class);
            System.out.println(dir);
            model = SavedModelBundle.load(dir.toString(), neuralNetworkConfig.getTags());
        }
        catch (TensorFlowException exception) {
            log.warn(exception.getMessage());
            throw new RuntimeException();
        } catch (IOException e) {
            throw new RuntimeException(e);
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


    public void loadModel() {

    }

    public float[] predict(float[] samples) {
        try (TFloat32 vector = TFloat32.vectorOf(samples);
             Tensor inputs = Tensor.of(
                     TFloat32.class,
                     Shape.of(1).append(INPUT_SIZE),
                     vector.asRawTensor().data());
             Result result = session.runner()
                     .feed(neuralNetworkConfig.getFeed(), inputs)
                     .fetch(neuralNetworkConfig.getFetch())
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
