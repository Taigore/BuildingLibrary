package taigore.buildapi.utils;


public enum MagicalEffect
{
	SPEED(1),
	SLOWNESS(2),
	HASTE(3),
	STRENGTH(5),
	JUMP_BOOST(8),
	REGENERATION(10),
	RESISTANCE(11),
	FIRE_RESISTANCE(12),
	INVISIBILITY(14),
	NIGHT_VISION(16),
	WEAKNESS(18),
	POISON(19);
	
	public final int id;
	
	MagicalEffect(int effectId) { this.id = effectId; }
}