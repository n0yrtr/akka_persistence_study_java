package nnishida.akka.persistence.study.java.akka.persistence;

import nnishida.akka.persistence.study.java.domain.Member;

import java.io.Serializable;

public class MemberEdited implements CommandAndEvent.Event, Serializable {
    public final Member member;

    public MemberEdited(Member member) {
        this.member = member;
    }
}
