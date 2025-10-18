package kogasastudio.ashihara.client.gui3d.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public record OBB(Vector3f center, Vector3f minXYZ, Vector3f maxXYZ, Matrix4f pose) {}
