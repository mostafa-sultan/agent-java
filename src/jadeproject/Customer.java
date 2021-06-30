/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeproject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.*;
/**
 *
 * @author Mostafa Sultan
 */
public class Customer extends Agent{
    static double balance=1000;
    static String productname;
    static int quantityofproduct;
    
    Scanner input=new Scanner(System.in);
    public void recive(){
    
     addBehaviour(new CyclicBehaviour() {
             @Override
             public void action() {
                ACLMessage acl=    receive();
//                 ACLMessage reply=msg.createReply();
                if( acl!= null){
                    
//                    System.out.println(acl.getPerformative());
                String content=acl.getContent();
                 System.out.println(content);
                 
             }
             
             }
         });
    
    
    }
    
    
    @Override
    protected void setup(){
         recive();
        System.out.println("customer created");
//        Object[] args = getArguments();
        
//        if (args != null && args.length > 0) {
//			productname = (String) args[0];
                        System.out.println("which product you need: ");
                        productname=input.next();
                        
                        System.out.println("Target product is "+productname);
                        System.out.println("quantity of product "+productname+" you need: ");
			quantityofproduct=input.nextInt();
        
        ACLMessage msg = new ACLMessage();
        msg.addReceiver(new AID("1", false));
        msg.setContent(quantityofproduct+"of "+productname);
        msg.setPerformative(ACLMessage.QUERY_IF);
        send(msg);
//        }
        
        
//        recive();
    }
    
    @Override
     protected void takeDown(){
         System.out.println("Customer Agent Terminated");
     
     
     }
    
    
}
