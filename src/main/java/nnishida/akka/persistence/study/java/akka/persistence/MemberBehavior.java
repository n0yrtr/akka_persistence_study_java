package nnishida.akka.persistence.study.java.akka.persistence;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.EventSourcedBehavior;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.EventHandler;
import nnishida.akka.persistence.study.java.domain.Member;

import java.util.HashMap;
import java.util.Map;

public class MemberBehavior extends EventSourcedBehavior<CommandAndEvent.Command, CommandAndEvent.Event, Map<String, Member>> {
    // this makes the context available to the command handler etc.
    private final ActorContext<CommandAndEvent.Command> context;

    // optionally if you only need `ActorContext.getSelf()`
    private final ActorRef<CommandAndEvent.Command> self;
    public MemberBehavior(PersistenceId persistenceId, ActorContext<CommandAndEvent.Command> ctx) {
        super(persistenceId);
        this.context = ctx;
        this.self = ctx.getSelf();
    }

    @Override
    public Map<String, Member> emptyState() {
        return new HashMap<>();
    }

    @Override
    public CommandHandler<CommandAndEvent.Command, CommandAndEvent.Event, Map<String, Member>> commandHandler() {
        return newCommandHandlerBuilder()
                .forAnyState()
                .onCommand(CommandAndEvent.RegisterMember.class, (state, cmd) -> {
                    return Effect().persist(new MemberRegistered(cmd.member));
                })
                .onCommand(CommandAndEvent.EditMember.class, (state, cmd) -> {
                    return Effect().persist(new MemberEdited(cmd.member));
                })
                .onCommand(CommandAndEvent.GetMember.class, (state, cmd) -> {
                    Member member = state.get(cmd.memberId);
                    if (member != null) {
                        cmd.replyTo.tell(member);
                    }
                    return Effect().none();
                })
                .build();
    }

    @Override
    public EventHandler<Map<String, Member>, CommandAndEvent.Event> eventHandler() {
        return newEventHandlerBuilder()
                .forAnyState()
                .onEvent(MemberRegistered.class, (state, evt) -> {
                    state.put(evt.member.getId(), evt.member);
                    return state;
                })
                .onEvent(MemberEdited.class, (state, evt) -> {
                    state.put(evt.member.getId(), evt.member);
                    return state;
                })
                .build();
    }

    public static Behavior<CommandAndEvent.Command> create(PersistenceId persistenceId) {
        return Behaviors.setup(context -> new MemberBehavior(persistenceId, context));
    }

}

