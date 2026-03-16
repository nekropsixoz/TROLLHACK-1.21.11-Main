package ru.noxium.module.impl.combat.auraProcess.rotationProcess.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.noxium.util.other.IMinecraft;

import java.util.concurrent.ThreadLocalRandom;

@Environment(EnvType.CLIENT)
public class NeuroAuraExec implements IMinecraft {
    private static final NeuroAuraLearn neuralNetwork = new NeuroAuraLearn();
    
    private static float lastYaw = 0;
    private static float lastPitch = 0;
    private static long lastUpdateTime = 0;
    
    public static Rotation calculatePolarRotation(LivingEntity target, boolean canAttack) {
        if (target == null || mc.player == null) {
            return new Rotation(mc.player.getYaw(), mc.player.getPitch());
        }
        
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;
        
        // Получаем позицию цели с предсказанием
        Vec3d targetPos = predictTargetPosition(target, deltaTime);
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d direction = targetPos.subtract(eyePos);
        
        // Базовые углы к цели
        float targetYaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float targetPitch = (float) Math.toDegrees(Math.asin(-direction.normalize().y));
        targetPitch = MathHelper.clamp(targetPitch, -89.0f, 89.0f);
        
        // Используем нейросеть для расчета оптимальных углов
        float[] neuralInput = prepareNeuralInput(target, direction, deltaTime);
        float[] neuralOutput = neuralNetwork.predict(neuralInput);
        
        // Применяем коррекцию от нейросети
        float yawCorrection = neuralOutput[0] * 15.0f; // Ограничиваем коррекцию
        float pitchCorrection = neuralOutput[1] * 10.0f;
        
        targetYaw += yawCorrection;
        targetPitch += pitchCorrection;
        
        // Плавная интерполяция
        float currentYaw = mc.player.getYaw();
        float currentPitch = mc.player.getPitch();
        
        float yawDelta = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float pitchDelta = targetPitch - currentPitch;
        
        // Адаптивная скорость поворота
        float distance = mc.player.distanceTo(target);
        float speedMultiplier = calculateSpeedMultiplier(distance, canAttack);
        
        float yawSpeed = 45.0f * speedMultiplier;
        float pitchSpeed = 35.0f * speedMultiplier;
        
        // Добавляем небольшой рандом для обхода античита
        if (!canAttack) {
            yawSpeed *= 0.7f;
            pitchSpeed *= 0.7f;
        }
        
        float smoothYaw = currentYaw + MathHelper.clamp(yawDelta, -yawSpeed, yawSpeed);
        float smoothPitch = MathHelper.clamp(currentPitch + MathHelper.clamp(pitchDelta, -pitchSpeed, pitchSpeed), -89.0f, 89.0f);
        
        // Обучаем нейросеть на основе результата
        if (canAttack) {
            float hitSuccess = calculateHitSuccess(target);
            neuralNetwork.learn(neuralInput, hitSuccess);
        }
        
        lastYaw = smoothYaw;
        lastPitch = smoothPitch;
        
        return new Rotation(smoothYaw, smoothPitch);
    }
    
    private static Vec3d predictTargetPosition(LivingEntity target, float deltaTime) {
        Vec3d currentPos = new Vec3d(target.getX(), target.getY() + target.getEyeHeight(target.getPose()) * 0.9, target.getZ());
        Vec3d velocity = target.getVelocity();
        
        // Предсказываем позицию на основе скорости
        float predictionTime = MathHelper.clamp(deltaTime * 3.0f, 0.05f, 0.2f);
        Vec3d predictedPos = currentPos.add(velocity.multiply(predictionTime));
        
        return predictedPos;
    }
    
    private static float[] prepareNeuralInput(LivingEntity target, Vec3d direction, float deltaTime) {
        float[] input = new float[8];
        
        // Расстояние до цели
        input[0] = (float) direction.length() / 6.0f; // Нормализуем
        
        // Скорость цели
        Vec3d velocity = target.getVelocity();
        input[1] = (float) velocity.length();
        
        // Угловая разница
        float yawDelta = MathHelper.wrapDegrees(lastYaw - mc.player.getYaw());
        float pitchDelta = lastPitch - mc.player.getPitch();
        input[2] = yawDelta / 180.0f;
        input[3] = pitchDelta / 90.0f;
        
        // Время с последнего обновления
        input[4] = MathHelper.clamp(deltaTime, 0.0f, 1.0f);
        
        // Высота цели относительно игрока
        input[5] = (float) (target.getY() - mc.player.getY()) / 10.0f;
        
        // Движение игрока
        Vec3d playerVelocity = mc.player.getVelocity();
        input[6] = (float) playerVelocity.length();
        
        // Случайный фактор для разнообразия
        input[7] = ThreadLocalRandom.current().nextFloat() * 0.1f;
        
        return input;
    }
    
    private static float calculateSpeedMultiplier(float distance, boolean canAttack) {
        // Чем ближе цель, тем быстрее поворот
        float distanceFactor = MathHelper.clamp(1.0f - (distance / 6.0f), 0.3f, 1.0f);
        
        // Если можем атаковать, увеличиваем скорость
        float attackFactor = canAttack ? 1.3f : 1.0f;
        
        return distanceFactor * attackFactor;
    }
    
    private static float calculateHitSuccess(LivingEntity target) {
        // Оцениваем успешность попадания на основе точности прицеливания
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d targetPos = new Vec3d(target.getX(), target.getY() + target.getEyeHeight(target.getPose()) * 0.9, target.getZ());
        Vec3d direction = targetPos.subtract(eyePos).normalize();
        
        Vec3d lookVec = mc.player.getRotationVec(1.0f);
        double dotProduct = lookVec.dotProduct(direction);
        
        // Преобразуем в оценку от 0 до 1
        return (float) MathHelper.clamp((dotProduct + 1.0) / 2.0, 0.0, 1.0);
    }
}
