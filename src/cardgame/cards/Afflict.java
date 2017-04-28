/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame.cards;

import cardgame.AbstractCardEffect;
import cardgame.AttackStack;
import cardgame.Card;
import cardgame.CardGame;
import cardgame.Effect;
import cardgame.Player;
import cardgame.TriggerAction;
import cardgame.Creature;
import cardgame.CreatureDecorator;
import cardgame.Triggers;
import java.util.Scanner;



/**
 *
 * @author GerardBaholli
 */
public class Afflict implements Card {

    @Override
    public String name() {
        return "Afflict";
    }

    @Override
    public String type() {
        return "Instant";
    }

    @Override
    public String ruleText() {
        return "Target creature gets -1/-1 until end of turn";
    }

    @Override
    public boolean isInstant() {
         return true;
    }
    
    @Override
    public String toString(){
        return "Afflict" + "[ "+ruleText()+" ]";
    }
    
    @Override
    public Effect getEffect(Player owner) {
        return new AfflictEffect(owner,this);
    }
    
    private class AfflictEffect extends AbstractCardEffect implements TriggerAction {

        private Player targetPlayer;
        private Creature oldCreature, decoratedCreature;
        
        public AfflictEffect(Player owner,Card card){
            super(owner,card);
        }
        
        @Override
        public void resolve() {
            
            System.out.println(owner.name() + ": Afflict, select target card owner: (0) you, (1) opponent");
            Scanner input = new Scanner(System.in);
            int idx=2;
            
            do {
                idx = input.nextInt();
                
                if (idx==0){
                    targetPlayer=owner;
                    System.out.println(owner.name() + ": selected " + targetPlayer.name());

                } else if (idx==1) {
                    targetPlayer=(CardGame.instance.getPlayer((CardGame.instance.getPlayer(0) == owner)?1:0));
                    System.out.println(owner.name() + ": selected " + targetPlayer.name());

                }
                
            } while (idx!=0 && idx!=1);
            
            if(!targetPlayer.getCreatures().isEmpty()){
                System.out.println("Select the creature for which apply this effect:");
                int i=1;
                for(Creature c : targetPlayer.getCreatures()){
                    System.out.println(i + ") " + c.name() + "[" + c.getPower() + "/" + c.getToughness() +"]");
                    i++;
                }
                idx = input.nextInt();
                Player currentPlayer = CardGame.instance.getCurrentPlayer();
                AttackStack attackStack = CardGame.instance.getAttackStack();
                decoratedCreature = new CreatureDecorator(targetPlayer.getCreatures().get(idx-1),-1,-1);
                oldCreature = targetPlayer.getCreatures().remove(idx-1);
                if(decoratedCreature.getToughness()>0){
                    if (currentPlayer.currentPhaseId().toString().equals("COMBAT") && attackStack.getAttackStack().containsKey(oldCreature)){
                        System.out.println(decoratedCreature.name()+" creatura decorata"+"[" + decoratedCreature.getPower() + "/" + decoratedCreature.getToughness() +"]");
                        attackStack.switchCreature(oldCreature, decoratedCreature);
                    }
                    targetPlayer.getCreatures().add(decoratedCreature);
                }else {
                    attackStack.getAttackStack().remove(oldCreature);
                }
                    
                CardGame.instance.getTriggers().register(Triggers.END_FILTER, this);
            } else {
                System.out.println(targetPlayer.name()+" has no creatures");
            } 
        }

        @Override
        public void execute(Object args){
            if(targetPlayer.getCreatures().contains(decoratedCreature)){
                targetPlayer.getCreatures().remove(decoratedCreature);
                targetPlayer.getCreatures().add(oldCreature);
            }
        }
    }
    
}
