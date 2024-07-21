package com.dxh.globe3d.utils;

/**
 * Created by XHD on 2024/07/17
 */
public class GlobeUtil {
    /**
     * 计算两个点之间的距离
     *
     * @param x1 第一个点的x坐标
     * @param y1 第一个点的y坐标
     * @param z1 第一个点的z坐标
     * @param x2 第二个点的x坐标
     * @param y2 第二个点的y坐标
     * @param z2 第二个点的z坐标
     * @return 两点之间的距离
     */
    public static double calculateTwoPointDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * 空间坐标转球坐标
     *
     * @param globeCenterX球心
     * @param globeCenterY
     * @param globeCenterZ
     * @param x球面坐标
     * @param y
     * @param z
     * @return 球坐标 方位角a 仰角b 半径R
     */
    public static double[] convertRoomToGlobePoint(double globeCenterX, double globeCenterY, double globeCenterZ, double x, double y, double z) {
        double dx = x - globeCenterX;
        double dy = y - globeCenterY;
        double dz = z - globeCenterZ;
        // 计算半径
        double R = Math.sqrt(dx * dx + dy * dy + dz * dz);
        // 计算仰角
        double b = Math.acos(dz / R);
        // 计算方位角
        double a = Math.atan2(dy, dx);
        // 将仰角和方位角从弧度转换为角度
        b = Math.toDegrees(b);
        a = Math.toDegrees(a);
        return new double[]{a, b, R};
    }

    /**
     * 球坐标转空间坐标
     *
     * @param globeCenterX 球心X坐标
     * @param globeCenterY 球心Y坐标
     * @param globeCenterZ 球心Z坐标
     * @param a       方位角a
     * @param b       仰角b
     * @param R       球半径
     * @return 返回一个包含x, y, z坐标的float数组
     */
    public static double[] convertGlobeToRoomPoint(double globeCenterX, double globeCenterY, double globeCenterZ, double a, double b, double R) {
        // 将角度转换为弧度
        double alpha = a * Math.PI / 180;
        double beta = b * Math.PI / 180;

        // 计算球面坐标
        double x = globeCenterX + R * Math.sin(beta) * Math.cos(alpha);
        double y = globeCenterY + R * Math.sin(beta) * Math.sin(alpha);
        double z = globeCenterZ + R * Math.cos(beta);
        // 返回结果
        return new double[]{(float) x, (float) y, (float) z};
    }

    /**
     * 计算绕轴新坐标
     *
     * @param angle
     * @param x       球面坐标
     * @param y
     * @param z
     * @param globeCenterX 球心坐标
     * @param globeCenterY
     * @param globeCenterZ
     * @param x1      绕轴的两点坐标
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return 返回绕轴新坐标 x y z
     */
    public static double[] calculateRotateAxisNewPoint(double angle, double x, double y, double z, double globeCenterX, double globeCenterY, double globeCenterZ, double x1, double y1, double z1, double x2, double y2, double z2) {
        // 计算旋转轴
        double[] axis = {x2 - x1, y2 - y1, z2 - z1};
        double axisLength = Math.sqrt(axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2]);
        axis[0] /= axisLength;
        axis[1] /= axisLength;
        axis[2] /= axisLength;
        // 将角度转换为弧度
        double angleRadian = Math.toRadians(angle);
        // 计算旋转矩阵
        double[][] rotationMatrix = calculateRotationMatrix(axis, angleRadian);
        // 应用旋转矩阵
        double[] originalPoint = {x - globeCenterX, y - globeCenterY, z - globeCenterZ};
        double[] rotatedPoint = multiplyMatrixAndPoint(rotationMatrix, originalPoint);
        return new double[]{rotatedPoint[0] + globeCenterX, rotatedPoint[1] + globeCenterY, rotatedPoint[2] + globeCenterZ};
    }

    /**
     *
     * @param angle 旋转角度
     * @param x 老坐标
     * @param y
     * @param z
     * @param globeCenterX 球心
     * @param globeCenterY
     * @param globeCenterZ
     * @param startX 滑动起点
     * @param startY
     * @param startZ
     * @param endX 滑动终点
     * @param endY
     * @param endZ
     * @return 返回绕轴新坐标x y z
     */
    public static double[] calculateRotateAxisNewPoint2(double angle, double x, double y, double z, double globeCenterX, double globeCenterY, double globeCenterZ, double startX, double startY, double startZ, double endX, double endY, double endZ) {
        // 计算滑动方向向量
        double[] direction = {endX - startX, endY - startY, endZ - startZ};
        double directionLength = Math.sqrt(direction[0] * direction[0] + direction[1] * direction[1] + direction[2] * direction[2]);
        direction[0] /= directionLength;
        direction[1] /= directionLength;
        direction[2] /= directionLength;

        // 计算垂直于滑动方向的旋转轴
        double[] axis = {-direction[1], direction[0], 0};
        double axisLength = Math.sqrt(axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2]);
        axis[0] /= axisLength;
        axis[1] /= axisLength;
        axis[2] /= axisLength;

        // 将角度转换为弧度
        double angleRadian = Math.toRadians(angle);
        // 计算旋转矩阵
        double[][] rotationMatrix = calculateRotationMatrix(axis, angleRadian);
        // 应用旋转矩阵
        double[] originalPoint = {x - globeCenterX, y - globeCenterY, z - globeCenterZ};
        double[] rotatedPoint = multiplyMatrixAndPoint(rotationMatrix, originalPoint);
        return new double[]{rotatedPoint[0] + globeCenterX, rotatedPoint[1] + globeCenterY, rotatedPoint[2] + globeCenterZ};
    }

    private static double[][] calculateRotationMatrix(double[] axis, double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double t = 1 - c;

        double x = axis[0];
        double y = axis[1];
        double z = axis[2];

        double[][] rotationMatrix = new double[3][3];
        rotationMatrix[0][0] = c + x * x * t;
        rotationMatrix[0][1] = x * y * t - z * s;
        rotationMatrix[0][2] = x * z * t + y * s;

        rotationMatrix[1][0] = y * x * t + z * s;
        rotationMatrix[1][1] = c + y * y * t;
        rotationMatrix[1][2] = y * z * t - x * s;

        rotationMatrix[2][0] = z * x * t - y * s;
        rotationMatrix[2][1] = z * y * t + x * s;
        rotationMatrix[2][2] = c + z * z * t;

        return rotationMatrix;
    }

    private static double[] multiplyMatrixAndPoint(double[][] matrix, double[] point) {
        double[] result = new double[3];
        result[0] = matrix[0][0] * point[0] + matrix[0][1] * point[1] + matrix[0][2] * point[2];
        result[1] = matrix[1][0] * point[0] + matrix[1][1] * point[1] + matrix[1][2] * point[2];
        result[2] = matrix[2][0] * point[0] + matrix[2][1] * point[1] + matrix[2][2] * point[2];
        return result;
    }
}
