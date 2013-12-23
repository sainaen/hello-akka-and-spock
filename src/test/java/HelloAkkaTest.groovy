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

    def "telling name to Greeter should change its greeting message"() {
        given:
        final TestActorRef<HelloAkkaJava.Greeter> greeter = TestActorRef.create(system, Props.create(HelloAkkaJava.Greeter.class), "greeter1")

        when:
        greeter.tell(new HelloAkkaJava.Whom("user"), ActorRef.noSender())

        then:
        greeter.underlyingActor().greeting == "Hello, user!"
    }
}
