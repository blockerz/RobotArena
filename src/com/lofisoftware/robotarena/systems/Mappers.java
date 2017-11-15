package com.lofisoftware.robotarena.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.lofisoftware.robotarena.components.ActiveZoneComponent;
import com.lofisoftware.robotarena.components.AnimationComponent;
import com.lofisoftware.robotarena.components.BoundsComponent;
import com.lofisoftware.robotarena.components.CameraComponent;
import com.lofisoftware.robotarena.components.CharacterComponent;
import com.lofisoftware.robotarena.components.ColorComponent;
import com.lofisoftware.robotarena.components.CommandComponent;
import com.lofisoftware.robotarena.components.CompetitorComponent;
import com.lofisoftware.robotarena.components.EnemyBrainComponent;
import com.lofisoftware.robotarena.components.EnemyComponent;
import com.lofisoftware.robotarena.components.FieldOfViewComponent;
import com.lofisoftware.robotarena.components.HazardComponent;
import com.lofisoftware.robotarena.components.HeroComponent;
import com.lofisoftware.robotarena.components.InputComponent;
import com.lofisoftware.robotarena.components.ItemComponent;
import com.lofisoftware.robotarena.components.MeleeWeaponComponent;
import com.lofisoftware.robotarena.components.MovementComponent;
import com.lofisoftware.robotarena.components.OpponentBrainComponent;
import com.lofisoftware.robotarena.components.RemoveEntityComponent;
import com.lofisoftware.robotarena.components.StateComponent;
import com.lofisoftware.robotarena.components.StatsComponent;
import com.lofisoftware.robotarena.components.TextureComponent;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.components.TurnComponent;
import com.lofisoftware.robotarena.components.ZoneComponent;

public class Mappers {

	public static final ComponentMapper<ActiveZoneComponent> activeZoneComponent = ComponentMapper.getFor(ActiveZoneComponent.class);
	public static final ComponentMapper<AnimationComponent> animationComponent = ComponentMapper.getFor(AnimationComponent.class);
	public static final ComponentMapper<BoundsComponent> boundsComponent = ComponentMapper.getFor(BoundsComponent.class);
	public static final ComponentMapper<CameraComponent> cameraComponent = ComponentMapper.getFor(CameraComponent.class);
	public static final ComponentMapper<CharacterComponent> characterComponent = ComponentMapper.getFor(CharacterComponent.class);
	public static final ComponentMapper<ColorComponent> colorComponent = ComponentMapper.getFor(ColorComponent.class);
	public static final ComponentMapper<CommandComponent> commandComponent = ComponentMapper.getFor(CommandComponent.class);
	public static final ComponentMapper<CompetitorComponent> competitorComponent = ComponentMapper.getFor(CompetitorComponent.class);
	public static final ComponentMapper<EnemyBrainComponent> brainComponent = ComponentMapper.getFor(EnemyBrainComponent.class);
	public static final ComponentMapper<FieldOfViewComponent> fieldOfViewComponent = ComponentMapper.getFor(FieldOfViewComponent.class);
	public static final ComponentMapper<EnemyComponent> enemyComponent = ComponentMapper.getFor(EnemyComponent.class);
	public static final ComponentMapper<HazardComponent> hazardComponent = ComponentMapper.getFor(HazardComponent.class);
	public static final ComponentMapper<HeroComponent> heroComponent = ComponentMapper.getFor(HeroComponent.class);
	public static final ComponentMapper<InputComponent> inputComponent = ComponentMapper.getFor(InputComponent.class);
	public static final ComponentMapper<ItemComponent> itemComponent = ComponentMapper.getFor(ItemComponent.class);
	public static final ComponentMapper<MeleeWeaponComponent> meleeWeapon = ComponentMapper.getFor(MeleeWeaponComponent.class);
	public static final ComponentMapper<MovementComponent> movementComponent = ComponentMapper.getFor(MovementComponent.class);
	public static final ComponentMapper<OpponentBrainComponent> opponentBrainComponent = ComponentMapper.getFor(OpponentBrainComponent.class);
	public static final ComponentMapper<RemoveEntityComponent> removeEntityComponent = ComponentMapper.getFor(RemoveEntityComponent.class);
	public static final ComponentMapper<StateComponent> stateComponent = ComponentMapper.getFor(StateComponent.class);
	public static final ComponentMapper<StatsComponent> statsComponent = ComponentMapper.getFor(StatsComponent.class);
	public static final ComponentMapper<TextureComponent> textureComponent = ComponentMapper.getFor(TextureComponent.class);
	public static final ComponentMapper<TransformComponent> transformComponent = ComponentMapper.getFor(TransformComponent.class);
	public static final ComponentMapper<TurnComponent> turnComponent = ComponentMapper.getFor(TurnComponent.class);
	public static final ComponentMapper<ZoneComponent> zoneComponent = ComponentMapper.getFor(ZoneComponent.class);
	
}
