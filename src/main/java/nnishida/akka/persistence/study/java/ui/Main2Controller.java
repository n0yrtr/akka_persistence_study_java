package nnishida.akka.persistence.study.java.ui;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Adapter;
import akka.actor.typed.javadsl.AskPattern;
import akka.persistence.typed.PersistenceId;
import akka.util.Timeout;
import lombok.SneakyThrows;
import lombok.val;
import nnishida.akka.persistence.study.java.akka.HelloWorldMain;
import nnishida.akka.persistence.study.java.akka.persistence.CommandAndEvent;
import nnishida.akka.persistence.study.java.akka.persistence.MemberBehavior;
import nnishida.akka.persistence.study.java.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

@Controller
public class Main2Controller {
    private final ActorSystem<CommandAndEvent.Command> memberSystem = ActorSystem.create(MemberBehavior.create(PersistenceId.ofUniqueId("member")), "memberSystem");
    private final Duration timeout = Duration.ofSeconds(3);
    @GetMapping("")
    public String index() {
        return "index";
    }

    @SneakyThrows
    @GetMapping("detail/{id}")
    @ResponseBody
    public Member list(@PathVariable("id") String id) {

        CompletionStage<Member> futureMember = AskPattern.<CommandAndEvent.Command,  Member>ask(
                memberSystem,
                replyTo -> new CommandAndEvent.GetMember(id, replyTo),
                timeout,
                memberSystem.scheduler()
        ).thenApply(member -> (Member) member);



        //会員情報の処理（例：プリント）
        return futureMember.toCompletableFuture().get();

    }

    @PostMapping("post")
    public String post(@RequestParam String id,
                       @RequestParam String name,
                       @RequestParam String mailAddress) {
        CompletionStage<Member> futureMember = AskPattern.<CommandAndEvent.Command,  Member>ask(
                memberSystem,
                replyTo -> new CommandAndEvent.GetMember(id, replyTo),
                timeout,
                memberSystem.scheduler()
        ).thenApply(member -> (Member) member);


        futureMember.thenAccept(member -> {
            if(member.getId() == null) {
                memberSystem.tell(new CommandAndEvent.RegisterMember(new Member(id,  name, mailAddress)));
                return;
            }
            memberSystem.tell(new CommandAndEvent.EditMember(new Member(id,  name, mailAddress)));
        });
        return "index";
    }

    /**
     * persistence以前のアクター自体の実装を確認する
     * @return
     */
    @GetMapping("actortest")
    public String actortest() {
        final ActorSystem<HelloWorldMain.SayHello> system = ActorSystem.create(HelloWorldMain.create(), "hello");
        system.tell(new HelloWorldMain.SayHello("World"));
        system.tell(new HelloWorldMain.SayHello("Akka"));
        return "index";
    }


}

