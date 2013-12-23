package org.sainaen;

import akka.actor.*;
import scala.concurrent.duration.Duration;

import java.io.Serializable;

public class HelloAkkaJava {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("hello-akka");
        final ActorRef greeter = system.actorOf(Props.create(Greeter.class), "greeter");
        final Inbox inbox = Inbox.create(system);

        greeter.tell(new Whom("World"), ActorRef.noSender());
        inbox.send(greeter, new Greet());

        Greeting greeting = (Greeting) inbox.receive(Duration.create(5, "seconds"));
        System.out.println("Greeting: " + greeting.message);
    }

    public static class Greeter extends UntypedActor {
        String greeting = "";

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Whom) {
                greeting = "Hello, " + ((Whom) message).whom + "!";
            } else if (message instanceof Greet) {
                getSender().tell(new Greeting(greeting), getSelf());
            } else {
                unhandled(message);
            }
        }
    }

    public static class Greet implements Serializable {}

    public static class Whom implements Serializable {
        public final String whom;
        public Whom(String whom) {
            this.whom = whom;
        }
    }

    public static class Greeting implements Serializable {
        public final String message;
        public Greeting(String message) {
            this.message = message;
        }
    }
}
