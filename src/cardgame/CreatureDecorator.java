/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame;

import java.util.List;

/**
 *
 * @author GerardBaholli
 */
public class CreatureDecorator implements Creature {

    private Creature creature;
    private int bonusAttack, bonusDefense;
    protected Player owner;
    protected int damageLeft;
    
    
    public CreatureDecorator(Creature creature,int bonusAttack, int bonusDefense){
        this.creature = creature;
        this.bonusAttack = bonusAttack;
        this.bonusDefense = bonusDefense;
        this.owner=creature.getOwner();
        this.damageLeft=this.getToughness();
    }
    
    @Override
    public boolean tap() {
        return creature.tap();
    }

    @Override
    public boolean untap() {
        return creature.untap();
    }

    @Override
    public boolean isTapped() {
        return creature.isTapped();
    }

    @Override
    public void attack() {
        //
    }

    @Override
    public void defend(Creature c) {
        //
    }

    @Override
    public void inflictDamage(int dmg) {
        creature.inflictDamage(dmg);
    }

    @Override
    public void resetDamage() {
        creature.resetDamage();
    }

    @Override
    public int getPower() {
        return creature.getPower() + bonusAttack;
    }

    @Override
    public int getToughness() {
        return creature.getToughness() + bonusDefense;
    }
    
    @Override
    public int getDamageLeft() {
        return damageLeft;
    }

    @Override
    public List<Effect> effects() {
        return creature.effects();

    }

    @Override
    public List<Effect> avaliableEffects() {
        return creature.avaliableEffects();
    }

    @Override
    public String name() {
        return creature.name();
    }

    @Override
    public void insert() {
        CardGame.instance.getTriggers().trigger(Triggers.ENTER_CREATURE_FILTER,this);
    }

    @Override
    public void remove() {
        owner.getCreatures().remove(this);
        CardGame.instance.getTriggers().trigger(Triggers.EXIT_CREATURE_FILTER,this);
    }

    @Override
    public Player getOwner() {
        return this.owner;
    }

}
