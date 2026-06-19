package ch.chattrix.shared.event;

import ch.chattrix.shared.types.UserData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllUsersResultEvent {
    private boolean success;
    private String errorMessage;
    private List<UserData> users;
}
