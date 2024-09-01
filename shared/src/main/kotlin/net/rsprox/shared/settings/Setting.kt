package net.rsprox.shared.settings

public enum class Setting(
    public val group: SettingGroup,
    public val category: SettingCategory,
    public val label: String,
    public val enabled: Boolean,
    public val tooltip: String? = null,
) {
    PREFER_SINGLE_QUOTE_ON_STRINGS(
        SettingGroup.LOGGING,
        SettingCategory.PREFS,
        "Prefer single quote",
        false,
        "When enabled, uses single quote whenever logging string-types. Otherwise, sticks to double quote.",
    ),
    TRANSLATE_INSTANCED_COORDS(
        SettingGroup.LOGGING,
        SettingCategory.MISC,
        "Translate Instanced Coords",
        true,
        "Translates instanced coords to the static counterparts if possible.",
    ),
    HIDE_UNNECESSARY_VARPS(
        SettingGroup.LOGGING,
        SettingCategory.MISC,
        "Hide Unnecessary Varps",
        true,
        "Hides the varp packet header if all the bit changes can be explained by varbits.",
    ),
    COLLAPSE_CLIENTSCRIPT_PARAMS(
        SettingGroup.LOGGING,
        SettingCategory.MISC,
        "Collapse Clientscript Params",
        true,
        "Collapses clientscript params & types by putting them all in one line.",
    ),

    PLAYER_EXT_INFO_INLINE(
        SettingGroup.LOGGING,
        SettingCategory.PLAYER_INFO,
        "Ext. Info Player Indicator",
        false,
        "Adds a short entry in-front of any extended info blocks, indicating the player on whom it is applied.",
    ),
    PLAYER_HIDE_INDEX(
        SettingGroup.LOGGING,
        SettingCategory.PLAYER_INFO,
        "Hide Player Indices",
        false,
        "Hides the index property in player info logs for all players.",
    ),
    PLAYER_REMOVAL(
        SettingGroup.LOGGING,
        SettingCategory.PLAYER_INFO,
        "Player Removal",
        false,
        "Whether to log players being being removed from your view.",
    ),
    PLAYER_INFO_HIDE_INACTIVE_PLAYERS(
        SettingGroup.LOGGING,
        SettingCategory.PLAYER_INFO,
        "Hide Inactive Players",
        true,
        "Hides any players who do not have an active extended info block applied. " +
            "This will hide players that are moving if they do not have extended info applied.",
    ),
    PLAYER_INFO_HIDE_EMPTY(
        SettingGroup.LOGGING,
        SettingCategory.PLAYER_INFO,
        "Hide Empty Player Info",
        true,
        "Hides the player info packet entry if there are no logged entries to show.",
    ),
    PLAYER_INFO_LOCAL_PLAYER_ONLY(
        SettingGroup.LOGGING,
        SettingCategory.PLAYER_INFO,
        "Local Player Only",
        true,
        "Only show player info updates for the local player - everyone else shall be skipped.",
    ),
    NPC_EXT_INFO_INDICATOR(
        SettingGroup.LOGGING,
        SettingCategory.NPC_INFO,
        "Ext. Info NPC Indicator",
        false,
        "Adds a short entry in-front of any extended info blocks, indicating the NPC on whom it is applied.",
    ),
    HIDE_NPC_INDICES(
        SettingGroup.LOGGING,
        SettingCategory.NPC_INFO,
        "Hide NPC Indices",
        false,
        "Hides the index property in NPC info logs for all NPCs.",
    ),
    NPC_REMOVAL(
        SettingGroup.LOGGING,
        SettingCategory.NPC_INFO,
        "Npc Removal",
        false,
        "Whether to log NPCs being being removed from your view.",
    ),
    NPC_INFO_HIDE_INACTIVE_NPCS(
        SettingGroup.LOGGING,
        SettingCategory.NPC_INFO,
        "Hide Inactive NPCs",
        true,
        "Hides any NPCs who do not have an active extended info block applied. " +
            "This will hide NPCs that are moving if they do not have extended info applied.",
    ),
    NPC_INFO_HIDE_EMPTY(
        SettingGroup.LOGGING,
        SettingCategory.NPC_INFO,
        "Hide Empty Npc Info",
        true,
        "Hides the NPC info packet entry if there are no logged entries to show.",
    ),
}
