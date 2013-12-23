import akka.actor.Inbox
import akka.testkit.TestActor
import junit.framework.Test
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestActorRef
import org.sainaen.HelloAkkaJava
import spock.lang.Shared
import spock.lang.Specification

class HelloAkkaTest extends Specification {
    @Shared ActorSystem system

    def setupSpec() {
        system = ActorSystem.create("testakka")
    }

    def cleanupSpec() {
        system.shutdown()
        system.awaitTermination(Duration.create("10 seconds"))
    }

    final String format = "Hello, %s!"
    final String name = "user"

    def "telling name to Greeter should change its state"() {
        given: "there is Greeter actor"
        final TestActorRef<HelloAkkaJava.Greeter> greeter = TestActorRef.create(system, Props.create(HelloAkkaJava.Greeter.class), "greeter1")

        when: "Greeter received name and format messages"
        greeter.tell(new HelloAkkaJava.GreetFormat(format), ActorRef.noSender())
        greeter.tell(new HelloAkkaJava.Whom(name), ActorRef.noSender())

        then: "Greeter's state should change"
        greeter.underlyingActor().format.format == format
        greeter.underlyingActor().whomToGreet.whom == name
    }

    def "with format and name set Greeter should be able to consutruct Greeting"() {
        given: "there is Greeter and Inbox actors"
        final TestActorRef<HelloAkkaJava.Greeter> greeter = TestActorRef.create(system, Props.create(HelloAkkaJava.Greeter.class), "greeter2")
        final Inbox inbox = Inbox.create(system)

        and: "format and name is set"
        greeter.tell(new HelloAkkaJava.GreetFormat(format), ActorRef.noSender())
        greeter.tell(new HelloAkkaJava.Whom(name), ActorRef.noSender())

        when: "Inbox sends request for the Greeting"
        inbox.send(greeter, new HelloAkkaJava.Greet())

        then: "Inbox should receive Greeting"
        ((HelloAkkaJava.Greeting) inbox.receive(Duration.create("1 second"))).message == String.format(format, name)
    }
}
