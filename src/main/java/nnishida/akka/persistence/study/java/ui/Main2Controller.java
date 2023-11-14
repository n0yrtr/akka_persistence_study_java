package nnishida.akka.persistence.study.java.ui;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Adapter;
import akka.actor.typed.javadsl.AskPattern;
import akka.persistence.typed.PersistenceId;
import akka.util.Timeout;
import lombok.val;
import nnishida.akka.persistence.study.java.akka.HelloWorldMain;
import nnishida.akka.persistence.study.java.akka.persistence.CommandAndEvent;
import nnishida.akka.persistence.study.java.akka.persistence.MemberBehavior;
import nnishida.akka.persistence.study.java.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

@Controller
public class Main2Controller {
    ActorSystem<CommandAndEvent.Command> memberSystem = ActorSystem.create(MemberBehavior.create(PersistenceId.ofUniqueId("member")), "memberSystem");


    @GetMapping("/")
    public String index() {

        val timeout = Duration.ofSeconds(3);
        CompletionStage<Member> futureMember = AskPattern.<CommandAndEvent.Command,  Member>ask(
                memberSystem,
                replyTo -> new CommandAndEvent.GetMember("3", replyTo),
                timeout,
                memberSystem.scheduler()
        ).thenApply(member -> (Member) member);

        //会員情報の処理（例：プリント）
        futureMember.thenAccept(member -> System.out.println("Member info: " + member));

        CompletionStage<Member> futureMember4 = AskPattern.<CommandAndEvent.Command,  Member>ask(
                memberSystem,
                replyTo -> new CommandAndEvent.GetMember("4", replyTo),
                timeout,
                memberSystem.scheduler()
        ).thenApply(member -> (Member) member);

        //会員情報の処理（例：プリント）
        futureMember4.thenAccept(member -> System.out.println("Member info: " + member));
        return "index";
    }

    @GetMapping("post")
    public String post() {
        memberSystem.tell(new CommandAndEvent.RegisterMember(new Member("1", " ほげほげ ほげお", "hogehoge@test.mail.com")));
        memberSystem.tell(new CommandAndEvent.EditMember(new Member("1", "フガフガ フガオ", "fugagfuga@test.mail.com")));
        memberSystem.tell(new CommandAndEvent.RegisterMember(new Member("2", "ピヨピヨ ピヨ子", "piyopiyo@zyyx.jp")));
        return "index";
    }

    //        final ActorSystem<HelloWorldMain.SayHello> system =
    //        ActorSystem.create(HelloWorldMain.create(), "hello");
    //
    //        system.tell(new HelloWorldMain.SayHello("World"));
    //        system.tell(new HelloWorldMain.SayHello("Akka"));
}

