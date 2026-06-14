package kogasastudio.ashihara.registry;

import com.geckolib.loading.math.MathParser;
import com.geckolib.loading.math.MolangQueries;
import com.geckolib.loading.math.value.Variable;
import kogasastudio.ashihara.Ashihara;
import kogasastudio.ashihara.client.models.geo.FermentationDisplayModel;
import kogasastudio.ashihara.client.models.geo.PotModel;
import kogasastudio.ashihara.client.models.geo.SelectionFrameModel;

public class MolangValues
{
    public static void registerMolangValues()
    {
        MathParser.registerVariable(new Variable("test", Ashihara.TEST_VALUE));

        MolangQueries.<SelectionFrameModel>setActorVariable("obb.min_x", obbActor -> obbActor.animatable().getOBB().minXYZ().x() * 16);
        MolangQueries.<SelectionFrameModel>setActorVariable("obb.min_y", obbActor -> obbActor.animatable().getOBB().minXYZ().y() * 16);
        MolangQueries.<SelectionFrameModel>setActorVariable("obb.min_z", obbActor -> obbActor.animatable().getOBB().minXYZ().z() * 16);
        MolangQueries.<SelectionFrameModel>setActorVariable("obb.max_x", obbActor -> obbActor.animatable().getOBB().maxXYZ().x() * 16);
        MolangQueries.<SelectionFrameModel>setActorVariable("obb.max_y", obbActor -> obbActor.animatable().getOBB().maxXYZ().y() * 16);
        MolangQueries.<SelectionFrameModel>setActorVariable("obb.max_z", obbActor -> obbActor.animatable().getOBB().maxXYZ().z() * 16);
        MolangQueries.<SelectionFrameModel>setActorVariable("selection_frame.thickness", sf -> sf.animatable().getThickness());

        MolangQueries.<PotModel>setActorVariable("pot.fluid_level_cur", pot -> pot.animatable().getLevelCurrent());
        MolangQueries.<PotModel>setActorVariable("pot.fluid_level_tgt", pot -> pot.animatable().getLevelTarget());

        MolangQueries.<FermentationDisplayModel>setActorVariable("vat.fluid_level_cur", fd -> fd.animatable().getLevelCurrent());
        MolangQueries.<FermentationDisplayModel>setActorVariable("vat.fluid_level_tgt", fd -> fd.animatable().getLevelTarget());
    }
}
