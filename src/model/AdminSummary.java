package model;

import java.util.List;

/** Everything the admin dashboard needs to render itself. */
public record AdminSummary(String name, List<StatInfo> stats, List<UserInfo> recentUsers,
                           List<SystemStatus> statuses, List<LanguageInfo> languages) {

    public AdminSummary {
        stats = List.copyOf(stats);
        recentUsers = List.copyOf(recentUsers);
        statuses = List.copyOf(statuses);
        languages = List.copyOf(languages);
    }

    public AdminSummary(List<StatInfo> stats, List<UserInfo> recentUsers) {
        this("Admin", stats, recentUsers, List.of(), List.of());
    }
}
