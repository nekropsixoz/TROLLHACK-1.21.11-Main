package ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.ThreadLocalRandom;

@Environment(EnvType.CLIENT)
public class NeuroAuraLearn {
    private static final int INPUT_SIZE = 8;
    private static final int HIDDEN_SIZE = 16;
    private static final int OUTPUT_SIZE = 2;
    
    private static final float LEARNING_RATE = 0.01f;
    
    // Веса нейросети
    private float[][] weightsInputHidden;
    private float[][] weightsHiddenOutput;
    private float[] biasHidden;
    private float[] biasOutput;
    
    // Кэш для обратного распространения
    private float[] lastInput;
    private float[] lastHidden;
    private float[] lastOutput;
    
    public NeuroAuraLearn() {
        initializeWeights();
    }
    
    private void initializeWeights() {
        weightsInputHidden = new float[INPUT_SIZE][HIDDEN_SIZE];
        weightsHiddenOutput = new float[HIDDEN_SIZE][OUTPUT_SIZE];
        biasHidden = new float[HIDDEN_SIZE];
        biasOutput = new float[OUTPUT_SIZE];
        
        // Инициализация весов случайными значениями
        for (int i = 0; i < INPUT_SIZE; i++) {
            for (int j = 0; j < HIDDEN_SIZE; j++) {
                weightsInputHidden[i][j] = randomWeight();
            }
        }
        
        for (int i = 0; i < HIDDEN_SIZE; i++) {
            for (int j = 0; j < OUTPUT_SIZE; j++) {
                weightsHiddenOutput[i][j] = randomWeight();
            }
            biasHidden[i] = randomWeight();
        }
        
        for (int i = 0; i < OUTPUT_SIZE; i++) {
            biasOutput[i] = randomWeight();
        }
    }
    
    private float randomWeight() {
        return ThreadLocalRandom.current().nextFloat() * 0.2f - 0.1f;
    }
    
    public float[] predict(float[] input) {
        if (input.length != INPUT_SIZE) {
            throw new IllegalArgumentException("Input size must be " + INPUT_SIZE);
        }
        
        lastInput = input.clone();
        
        // Скрытый слой
        lastHidden = new float[HIDDEN_SIZE];
        for (int i = 0; i < HIDDEN_SIZE; i++) {
            float sum = biasHidden[i];
            for (int j = 0; j < INPUT_SIZE; j++) {
                sum += input[j] * weightsInputHidden[j][i];
            }
            lastHidden[i] = relu(sum);
        }
        
        // Выходной слой
        lastOutput = new float[OUTPUT_SIZE];
        for (int i = 0; i < OUTPUT_SIZE; i++) {
            float sum = biasOutput[i];
            for (int j = 0; j < HIDDEN_SIZE; j++) {
                sum += lastHidden[j] * weightsHiddenOutput[j][i];
            }
            lastOutput[i] = tanh(sum);
        }
        
        return lastOutput;
    }
    
    public void learn(float[] input, float reward) {
        if (lastInput == null || lastHidden == null || lastOutput == null) {
            return;
        }
        
        // Простое обучение с подкреплением
        float error = reward - 0.5f;
        
        // Обновляем веса выходного слоя
        for (int i = 0; i < HIDDEN_SIZE; i++) {
            for (int j = 0; j < OUTPUT_SIZE; j++) {
                float gradient = error * lastHidden[i] * tanhDerivative(lastOutput[j]);
                weightsHiddenOutput[i][j] += LEARNING_RATE * gradient;
                weightsHiddenOutput[i][j] = MathHelper.clamp(weightsHiddenOutput[i][j], -1.0f, 1.0f);
            }
        }
        
        // Обновляем bias выходного слоя
        for (int i = 0; i < OUTPUT_SIZE; i++) {
            float gradient = error * tanhDerivative(lastOutput[i]);
            biasOutput[i] += LEARNING_RATE * gradient;
            biasOutput[i] = MathHelper.clamp(biasOutput[i], -1.0f, 1.0f);
        }
        
        // Обновляем веса скрытого слоя
        for (int i = 0; i < INPUT_SIZE; i++) {
            for (int j = 0; j < HIDDEN_SIZE; j++) {
                float gradient = error * lastInput[i] * reluDerivative(lastHidden[j]);
                weightsInputHidden[i][j] += LEARNING_RATE * gradient * 0.5f;
                weightsInputHidden[i][j] = MathHelper.clamp(weightsInputHidden[i][j], -1.0f, 1.0f);
            }
        }
    }
    
    // Функция активации ReLU
    private float relu(float x) {
        return Math.max(0, x);
    }
    
    private float reluDerivative(float x) {
        return x > 0 ? 1.0f : 0.0f;
    }
    
    // Функция активации tanh
    private float tanh(float x) {
        return (float) Math.tanh(x);
    }
    
    private float tanhDerivative(float x) {
        float tanh = tanh(x);
        return 1.0f - tanh * tanh;
    }
}
