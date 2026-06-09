package ch.bbw.chattrix.dto.chat;

import ch.bbw.chattrix.entity.Member;

import java.util.List;

public record ChatGroupRequest(List<Member> members, String name) {
}
