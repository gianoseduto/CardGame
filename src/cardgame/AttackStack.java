/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fgiannuz
 */
public class AttackStack {
    private Map<Creature, ArrayList<Creature>> attackStack;
    
    public void Attackstack(){
            this.attackStack = new HashMap<>();
        }
    public void addAttacker(Creature c){
            attackStack.put(c, new ArrayList<Creature>());
        }
    public void addDefender(Creature c, Creature d){
           for(Map.Entry<Creature,ArrayList<Creature>> fight : attackStack.entrySet()){
               if(fight.getKey().equals(c)){
                   fight.getValue().add(d);
               }
           }
    }
    public Map<Creature, ArrayList<Creature>> getAttackStack(){
            return attackStack;
    }
    public void clean(){
            this.attackStack= new HashMap<>();
    }
    //cambia la creatura con il suo Decoratore
    public void switchCreature(Creature c,Creature b){
            for(Map.Entry<Creature,ArrayList<Creature>> fight : attackStack.entrySet()){
               if(fight.getKey().equals(c)){
                   ArrayList<Creature> defenders = fight.getValue();
                   attackStack.remove(fight.getKey());
                   addAttacker(b);
                   attackStack.replace(b,defenders);
                   
               }
           }
    }
}
