package nnishida.akka.persistence.study.java.akka.persistence;

import nnishida.akka.persistence.study.java.domain.Member;

import java.io.Serializable;

public class MemberRegistered implements CommandAndEvent.Event, Serializable {
    public final Member member;

    public MemberRegistered(Member member) {
        this.member = member;
    }
}
