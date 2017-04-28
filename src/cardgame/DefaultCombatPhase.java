/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author atorsell
 */
public class DefaultCombatPhase implements Phase {

    private void inflictFaceDamage(Map<Creature,ArrayList<Creature>> creatureStack, Player target){
        for (Map.Entry<Creature,ArrayList<Creature>> fight : creatureStack.entrySet()){
            if (fight.getValue().isEmpty()) {
                target.inflictDamage(fight.getKey().getPower());
            }
        }
    }

    private void fightResolver(Map<Creature,ArrayList<Creature>> creatureStack){
        int i, att_dmg;
        Creature att,deff;
        boolean exit;
        for (Map.Entry<Creature,ArrayList<Creature>> fight : creatureStack.entrySet()){
            exit = false;
            att = fight.getKey();
            att_dmg = att.getPower();
            i = 0;
            System.out.println(att.name()+" attaccante [" + att.getPower()+"/"+att.getToughness()+"]");
            while (i < (fight.getValue().size()) && !exit){
                deff = fight.getValue().get(i);
                  System.out.println(att.name()+" [" + att.getPower()+"/"+att.getDamageLeft() +"] combatte contro " + deff.name()+ "[" + att.getPower()+"/"+att.getToughness()+"]");
                if (att.getDamageLeft() > 0){
                    if (deff.getDamageLeft() > att_dmg){
                        deff.inflictDamage((att.getPower()));
                        att.inflictDamage(deff.getPower());
                        exit = true;
                    } else {
                        att_dmg = att_dmg - deff.getDamageLeft();
                        deff.inflictDamage(deff.getDamageLeft());
                        att.inflictDamage(deff.getPower());
                    }
                }
                i++;
            }
        }
    } 

    @Override
    public void execute() {
        Scanner input;
        int i, idx;
        AttackStack attackStack = CardGame.instance.getAttackStack();
        Player currentPlayer = CardGame.instance.getCurrentPlayer();
        Player currentAdversary = CardGame.instance.getCurrentAdversary();

        System.out.println("-- " + currentPlayer.name() + ": combat phase --");

        CardGame.instance.getTriggers().trigger(Triggers.COMBAT_FILTER);
        ArrayList<Creature> adversaryCreatures, playerCreatures, fighters;

        if (currentPlayer.getCreatures().isEmpty()){
            System.out.println("There aren't creatures on your field");
        } else {
            attackStack.clean();
            playerCreatures = new ArrayList<>(currentPlayer.getCreatures());
            do {
                //stampa terreno
                System.out.println(currentPlayer.name()+ ": select creature to attack, 0 to pass");
                i = 1;
                fighters = new ArrayList<>();
                for(Creature c : playerCreatures){
                    if(c.getPower() > 0){
                        System.out.println(i + ") " + c.name());
                        i++;
                        fighters.add(c);
                    }
                }
                
                //acquisizione input
                input = new Scanner(System.in);
                idx= input.nextInt();

                if(idx>0 && idx<=i){
                    attackStack.addAttacker(fighters.get(idx-1));
                    playerCreatures.remove(fighters.get(idx-1));
                }
                else if(idx>i){
                    System.out.println("Exception : Invalid Input, your input is out of range");
                }
            } while(idx>0 && !fighters.isEmpty());
            
            CardGame.instance.getTriggers().trigger(Triggers.COMBAT_FILTER);
            
            //codice per la richiesta circolare di utilizzo di istantanee
            int numberPasses=0;
            int responsePlayerIdx = (CardGame.instance.getPlayer(0) == currentPlayer)?1:0;
            
        
            while (numberPasses<2) {
                if (playAvailableEffect(CardGame.instance.getPlayer(responsePlayerIdx)))
                    numberPasses=0;
                else ++numberPasses;
            
                responsePlayerIdx = (responsePlayerIdx+1)%2;
            }
            
            CardGame.instance.getStack().resolve();
            adversaryCreatures = new ArrayList<>(currentAdversary.getCreatures());

            if (adversaryCreatures.isEmpty()) {
                System.out.println(currentAdversary.name()+": there aren't creatures on your field to defend");
                System.out.println(currentAdversary.name()+": these creatures will attack you directly");
                
                for(Map.Entry<Creature,ArrayList<Creature>> fight : attackStack.getAttackStack().entrySet()){
                    System.out.println(fight.getKey().name() + "[" + fight.getKey().getPower() + "/" + fight.getKey().getToughness() +"]");
                }
            } else {
                System.out.println(currentAdversary.name() +": you have to defend from these creatures :");
                for(Map.Entry<Creature,ArrayList<Creature>> fight : attackStack.getAttackStack().entrySet()){
                    System.out.println(fight.getKey().name() + "[" + fight.getKey().getPower() + "/" + fight.getKey().getToughness() +"]");
                }
                for(Map.Entry<Creature,ArrayList<Creature>> fight : attackStack.getAttackStack().entrySet()){
                    do{
                        System.out.println(currentAdversary.name()+": select one creature or more to defend from : " +fight.getKey().name() + "[" + fight.getKey().getPower() + "/" + fight.getKey().getToughness() +"], 0 to pass");                        
                        i=1;
                        for(Creature c : adversaryCreatures){
                            System.out.println(i + ") " + c.name() + "[" + c.getPower() + "/" + c.getToughness() +"]");
                            i++;
                            fighters.add(c);
                            
                        }
                        
                        input = new Scanner(System.in);
                        idx= input.nextInt();
                        
                        if(idx>0 && idx<=i){
                            attackStack.addDefender(fight.getKey(),adversaryCreatures.remove(idx-1));
                        }
                    }while(!adversaryCreatures.isEmpty() && idx>0);                                                                               
                }
                
                
               
                inflictFaceDamage(attackStack.getAttackStack(), currentAdversary);
                fightResolver(attackStack.getAttackStack());
                
                System.out.println(currentPlayer.name() + ": Combat finished");
                
                //Codice per la stampa del campo DOPO il combattimento
                System.out.println("=== Field After Combat ===");
                for (int j=0; j!=2; ++j) {
                    Player fieldsPlayer=CardGame.instance.getPlayer(j);
                    List<Creature> creatures = fieldsPlayer.getCreatures();
                    System.out.println(fieldsPlayer.name() + " has :"+fieldsPlayer.getLife()+" life");
                    if (creatures.isEmpty()) {
                        System.out.println(fieldsPlayer.name() + " has no creature in play");
                    } else {
                        System.out.println(fieldsPlayer.name() + "'s creatures in play:");
                        for (Creature c:creatures)
                            System.out.println("  "+ c.name() + " [" + c.getPower()+"/" +c.getToughness() + "]");
                    }
                List<Enchantment> enchantments = fieldsPlayer.getEnchantments();
                    if (enchantments.isEmpty()) {
                    System.out.println(fieldsPlayer.name() + " has no enchantment in play");
                    } else {
                    System.out.println(fieldsPlayer.name() + "'s enchantments in play:");
                    for (Enchantment e:enchantments)
                        System.out.println("  "+e);
                    }
                }
        System.out.println("============="); 
            }
        }
    }
    
    private boolean playAvailableEffect(Player activePlayer) {
        //collect and display available effects...
        ArrayList<Effect> availableEffects = new ArrayList<>();
        Scanner reader = CardGame.instance.getScanner();

        //...cards first
        System.out.println(activePlayer.name() + " select istant card/effect to play, 0 to pass");
        int i=0;
        for( Card c:activePlayer.getHand() ) {
            if (c.isInstant() ) {
                availableEffects.add( c.getEffect(activePlayer) );
                System.out.println(Integer.toString(i+1)+") " + c );
                ++i;
            }
        }
        
        //...creature effects last
        for ( Creature c:activePlayer.getCreatures()) {
            for (Effect e:c.avaliableEffects()) {
                availableEffects.add(e);
                System.out.println(Integer.toString(i+1)+") " + c.name() + 
                    " ["+ e + "]" );
                ++i;
            }
        }
        
        //get user choice and play it
        int idx= reader.nextInt()-1;
        if (idx<0 || idx>=availableEffects.size()) return false;

        availableEffects.get(idx).play();
        return true;
    }



}

