package kogasastudio.ashihara.client.gui3d.debug;

import kogasastudio.ashihara.client.gui3d.Screen3D;
import kogasastudio.ashihara.client.gui3d.util.OBB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class Gui3dDebugProjector
{
	private Gui3dDebugProjector()
	{
	}

	public static Vector4f[] projectCorners(OBB obb, Screen3D screen)
	{
		Matrix4f pose = obb.pose();
		Vector3f min = obb.minXYZ();
		Vector3f max = obb.maxXYZ();
		Vector4f[] result = new Vector4f[8];
		int index = 0;

		float toGui = screen.getPickingGuiScale() * screen.getPickingPipScale();
		if (!Float.isFinite(toGui) || Math.abs(toGui) < 1e-6f)
		{
			return null;
		}

		for (int ix = 0; ix < 2; ix++)
		{
			for (int iy = 0; iy < 2; iy++)
			{
				for (int iz = 0; iz < 2; iz++)
				{
					float px = ix == 0 ? min.x : max.x;
					float py = iy == 0 ? min.y : max.y;
					float pz = iz == 0 ? min.z : max.z;
					Vector4f transformed = new Vector4f(px, py, pz, 1.0f).mul(pose);

					if (!Float.isFinite(transformed.w) || transformed.w == 0.0f)
					{
						return null;
					}

					float pipX = transformed.x / transformed.w;
					float pipY = transformed.y / transformed.w;
					float guiX = pipX / toGui + screen.getPickingPipX0();
					float guiY = pipY / toGui + screen.getPickingPipY0();
					result[index++] = new Vector4f(guiX, guiY, transformed.z / transformed.w, 1.0f);
				}
			}
		}

		return result;
	}

	public static Vector3f getProjectionCenter(OBB obb, Screen3D screen)
	{
		Vector4f[] corners = projectCorners(obb, screen);
		if (corners == null)
		{
			return null;
		}

		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		float depth = 0.0f;

		for (Vector4f corner : corners)
		{
			float cx = corner.x;
			float cy = corner.y;
			if (cx < minX) minX = cx;
			if (cy < minY) minY = cy;
			if (cx > maxX) maxX = cx;
			if (cy > maxY) maxY = cy;
			depth += corner.z;
		}

		return new Vector3f((minX + maxX) * 0.5f, (minY + maxY) * 0.5f, depth / corners.length);
	}
}

