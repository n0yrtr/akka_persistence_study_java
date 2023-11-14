package nnishida.akka.persistence.study.java.akka.persistence;

import akka.actor.typed.ActorRef;
import nnishida.akka.persistence.study.java.domain.Member;

import java.io.Serializable;

public class CommandAndEvent {
    public interface Command {}

    public static class GetMember implements Command {
        public final String memberId;
        public final ActorRef<Member> replyTo;

        public GetMember(String memberId, ActorRef<Member> replyTo) {
            this.memberId = memberId;
            this.replyTo = replyTo;
        }
    }

    public static class RegisterMember implements Command {
        public final Member member;

        public RegisterMember(Member member) {
            this.member = member;
        }
    }

    public static class EditMember implements Command {
        public final Member member;

        public EditMember(Member member) {
            this.member = member;
        }
    }

    public interface Event {}

}
