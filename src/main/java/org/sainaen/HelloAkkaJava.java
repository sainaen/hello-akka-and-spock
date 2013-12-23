package org.sainaen;

import akka.actor.*;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class HelloAkkaJava {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("hello-akka");
        final ActorRef greeter = system.actorOf(Props.create(Greeter.class), "greeter");
        final Inbox inbox = Inbox.create(system);

        greeter.tell(new Whom("World"), ActorRef.noSender());
        inbox.send(greeter, new Greet());
        Greeting greeting = (Greeting) inbox.receive(Duration.create(5, "seconds"));
        System.out.println("Greeting: " + greeting.message);

        final ActorRef printer = system.actorOf(Props.create(GreetPrinter.class), "printer");
        // schedule( initialDelay, interval, receiver, message, executor, sender );
        system.scheduler().schedule(Duration.Zero(), Duration.create(1, TimeUnit.SECONDS), greeter, new Greet(), system.dispatcher(), printer);
    }

    public static class Greeter extends UntypedActor {
        public GreetFormat format = new GreetFormat("");
        public Whom whomToGreet = new Whom("");

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Whom) {
                whomToGreet = (Whom) message;
            } else if (message instanceof GreetFormat) {
                format = (GreetFormat) message;
            } else if (message instanceof Greet) {
                getSender().tell(new Greeting(format, whomToGreet), getSelf());
            } else {
                unhandled(message);
            }
        }
    }

    public static class GreetPrinter extends UntypedActor {
        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof Greeting) {
                System.out.println(((Greeting) message).message);
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

    public static class GreetFormat implements Serializable {
        public final String format;
        public GreetFormat(String format) {
            this.format = format;
        }
    }

    public static class Greeting implements Serializable {
        public final String message;
        public Greeting(GreetFormat greetFormat, Whom whomToGreet) {
            this.message = String.format(greetFormat.format, whomToGreet.whom);
        }
    }
}
