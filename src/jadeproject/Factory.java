/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jadeproject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import static jade.tools.sniffer.Agent.i;

/**
 *
 * @author Mostafa Sultan
 * 
 */
public class Factory extends Agent {

    // ---------------------------------------
    String x = new String(new char[20]).replace("\0", "-");

    String[] products = {"A", "B", "C"};
    double[] prices = {30, 60, 120};
    int[] quantity = {10, 15, 20};
    static boolean flag = true;
    static boolean offerstate = true;

    public void productINFO(String[] products, double[] prices, int[] quantity) {

        System.out.println(x);
        System.out.println("products :");
        for (int i = 0; i < products.length; i++) {
            System.out.println("Product " + products[i] + " .price =" + prices[i] + "$ .Quantity =" + quantity[i]);

        }
        System.out.println(x);

    }

    public void offer(int time) {
        time = time * 1000; // sec =1000
        ACLMessage acl = new ACLMessage();
        acl.addReceiver(new AID("2", false));

        if (offerstate == true) {
            addBehaviour(new TickerBehaviour(this, time) {//180000

                @Override
                protected void onTick() {
//                System.out.println("Product prices update:");

                    if (offerstate) {
                        for (int i = 0; i < prices.length; i++) {
                            prices[i] = prices[i] - (prices[i] * .1);
                            acl.setPerformative(ACLMessage.INFORM);
                            acl.setContent("Discount:\nProduct " + products[i] + " .price =" + prices[i] + "$\n" + x);
                            send(acl);
                        }
                        offerstate = false;
                         
                    } else {
                            prices[0]=30;
                            prices[1]=60;
                            prices[2]=120;


                    }
                }

            });

//    }
        }
    }

    public void updatePRICES(int time) {
        time = time * 1000; // sec =1000
        ACLMessage acl = new ACLMessage();
        acl.addReceiver(new AID("2", false));

        addBehaviour(new TickerBehaviour(this, time) {//180000

            @Override
            protected void onTick() {
//                System.out.println("Product prices update:");
                for (int i = 0; i < prices.length; i++) {
                    prices[i] = prices[i] + (prices[i] * .1);
                    acl.setPerformative(ACLMessage.INFORM);
                    acl.setContent("Update:\nProduct " + products[i] + " .price =" + prices[i] + "$\n" + x);
                    send(acl);
                }
//               

            }

        });
    }

    public int[] content(String content) {

//        int num=Integer.parseInt(content.substring(0, content.indexOf("of")-1));
//        String productname=content.substring(content.indexOf("of")+3);
        String productname = Customer.productname;
        int productnameindex = 0;// product index in array
        int num = Customer.quantityofproduct;

        if (productname.contentEquals("A") || productname.contentEquals("a")) {
            productnameindex = 0;
        } else if (productname.contentEquals("B") || productname.contentEquals("b")) {
            productnameindex = 1;
        } else if (productname.contentEquals("C") || productname.contentEquals("c")) {
            productnameindex = 2;
        } else {
            System.out.println("product not found");
            return null;
        }

        return new int[]{num, productnameindex};

    }

    public void buy(int i, int msgarr[], ACLMessage reply) {
//        ACLMessage acl=receive();
//        ACLMessage reply=acl.createReply();
        if (Customer.balance >= (prices[i] * msgarr[0])) {
            System.out.println("Operation Accomplished Successfully \n" + x);
            if (msgarr[0] == 10 || msgarr[0] == 20) {
                // discount 10%
                System.out.println("you have 10% Discount\n" + x);
                prices[i] = prices[i] - (prices[i] * .1);
            }
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContent("total Cost: " + (prices[i] * msgarr[0]) + "$");

            Customer.balance = Customer.balance - (prices[i] * msgarr[0]);
            quantity[i] = quantity[i] - msgarr[0];

        } else if (Customer.balance < (prices[i] * msgarr[0])) {
            flag = false;
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContent("your balance not enough\nyou can get 2 and pay later");

            quantity[i] = quantity[i] - 2;

        }

    }

    public void recive() {

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage acl = receive();

                if (acl != null) {
                    ACLMessage reply = acl.createReply();
                    if (acl.getPerformative() == 12) {

                        String msgcontent = acl.getContent();
                        int msgarr[] = content(msgcontent);
                        System.out.println(x);
                        System.out.println("Customer msg content: I'd like to buy " + msgcontent);
                        System.out.println(x);

                        if (flag == true) {
                            // check if product is A

                            if (msgarr[1] == 0) {
                                if (msgarr[0] <= quantity[0]) {
                                    buy(0, msgarr, reply);
                                } else {
                                    reply.setPerformative(ACLMessage.PROPOSE);
                                    reply.setContent("only " + quantity[0] + " of " + products[0] + " avilable");

                                }

                                // check if product is B
                            } else if (msgarr[1] == 1) {
                                if (msgarr[0] <= quantity[1]) {
                                    buy(1, msgarr, reply);
                                } else {
                                    reply.setPerformative(ACLMessage.PROPOSE);
                                    reply.setContent("only " + quantity[1] + " of " + products[1] + " avilable");

                                }

                                // check if product is C
                            } else if (msgarr[1] == 2) {
                                if (msgarr[0] <= quantity[2]) {
                                    buy(2, msgarr, reply);
                                } else {
                                    reply.setPerformative(ACLMessage.PROPOSE);
                                    reply.setContent("only " + quantity[2] + " of " + products[2] + " avilable");

                                }

                            }

                            send(reply);
                        } else {
                            reply.setPerformative(ACLMessage.REFUSE);
                            reply.setContent("You have to pay first");
                            send(reply);
                        }
                        aftersell();
                    }
                }
            }
        }
        );
    }

    public void aftersell() {

        System.out.println("products INFO after sell");
        productINFO(products, prices, quantity);
        System.out.println(" customer new balance = " + Customer.balance + "$ \n" + x);

    }

    @Override
    protected void setup() {

        System.out.println("Fctory Agent Created\n");
        productINFO(products, prices, quantity);
//        updatePRICES(180);
        offer(30);
        recive();
//        

//       ACLMessage msg = new ACLMessage();
//        msg.addReceiver(new AID("3", false));
//        msg.setContent("send message test");
//        msg.setPerformative(ACLMessage.INFORM);
//        send(msg);
    }

    @Override
    protected void takeDown() {
        System.out.println("Factory Agent Terminated");

    }

}
