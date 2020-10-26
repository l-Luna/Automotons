package net.automotons.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FixedColorVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

/**
 * Copy of OverlayVertexConsumer that uses fixed colour correctly.
 */
@Environment(EnvType.CLIENT)
public class ColourVertexConsumer extends FixedColorVertexConsumer{
	private final VertexConsumer vertexConsumer;
	private final Matrix4f modelMatrix; // probably not technically correct
	private final Matrix3f normalMatrix;
	private float x;
	private float y;
	private float z;
	private int u1;
	private int v1;
	private int light;
	private float normalX;
	private float normalY;
	private float normalZ;
	
	public ColourVertexConsumer(VertexConsumer vertexConsumer, Matrix4f modelMatrix, Matrix3f normalMatrix){
		this.vertexConsumer = vertexConsumer;
		this.modelMatrix = modelMatrix.copy();
		this.modelMatrix.invert();
		this.normalMatrix = normalMatrix.copy();
		this.normalMatrix.invert();
		init();
	}
	
	private void init(){
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.u1 = 0;
		this.v1 = 10;
		this.light = 15728880;
		this.normalX = 0;
		this.normalY = 1;
		this.normalZ = 0;
	}
	
	public void next(){
		Vector3f vector3f = new Vector3f(normalX, normalY, normalZ);
		vector3f.transform(normalMatrix);
		Direction direction = Direction.getFacing(vector3f.getX(), vector3f.getY(), vector3f.getZ());
		Vector4f vector4f = new Vector4f(x, y, z, 1);
		vector4f.transform(modelMatrix);
		vector4f.rotate(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		vector4f.rotate(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
		vector4f.rotate(direction.getRotationQuaternion());
		float f = -vector4f.getX();
		float g = -vector4f.getY();
		vertexConsumer.vertex(x, y, z).color(fixedRed / 255f, fixedGreen / 255f, fixedBlue / 255f, fixedAlpha / 255f).texture(f, g).overlay(u1, v1).light(light).normal(normalX, normalY, normalZ).next();
		init();
	}
	
	public VertexConsumer vertex(double x, double y, double z){
		this.x = (float)x;
		this.y = (float)y;
		this.z = (float)z;
		return this;
	}
	
	public VertexConsumer color(int red, int green, int blue, int alpha){
		return this;
	}
	
	public VertexConsumer texture(float u, float v){
		return this;
	}
	
	public VertexConsumer overlay(int u, int v){
		this.u1 = u;
		this.v1 = v;
		return this;
	}
	
	public VertexConsumer light(int u, int v){
		this.light = u | v << 16;
		return this;
	}
	
	public VertexConsumer normal(float x, float y, float z){
		this.normalX = x;
		this.normalY = y;
		this.normalZ = z;
		return this;
	}
}