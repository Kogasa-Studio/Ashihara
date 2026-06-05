package kogasastudio.ashihara.block.furniture;

public enum FurnitureRenderPass
{
    /** 通过 chunk buffer 渲染（与建筑部件相同） */
    CHUNK_BUFFER,
    /** 每帧 BER 动态渲染（如容器内食物） */
    BER_DYNAMIC,
    /** GeckoLib 模型渲染（支持动画） */
    GECKOLIB,
}
