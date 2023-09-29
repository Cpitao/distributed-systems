from google.protobuf.internal import containers as _containers
from google.protobuf.internal import enum_type_wrapper as _enum_type_wrapper
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from typing import ClassVar as _ClassVar, Iterable as _Iterable, Mapping as _Mapping, Optional as _Optional, Union as _Union

DESCRIPTOR: _descriptor.FileDescriptor

class FootballGame(_message.Message):
    __slots__ = ["score", "scorers", "team1", "team2"]
    class FootballScore(_message.Message):
        __slots__ = ["team1Score", "team2Score"]
        TEAM1SCORE_FIELD_NUMBER: _ClassVar[int]
        TEAM2SCORE_FIELD_NUMBER: _ClassVar[int]
        team1Score: int
        team2Score: int
        def __init__(self, team1Score: _Optional[int] = ..., team2Score: _Optional[int] = ...) -> None: ...
    class Scorers(_message.Message):
        __slots__ = ["team1Scorers", "team2Scorers"]
        TEAM1SCORERS_FIELD_NUMBER: _ClassVar[int]
        TEAM2SCORERS_FIELD_NUMBER: _ClassVar[int]
        team1Scorers: _containers.RepeatedScalarFieldContainer[str]
        team2Scorers: _containers.RepeatedScalarFieldContainer[str]
        def __init__(self, team1Scorers: _Optional[_Iterable[str]] = ..., team2Scorers: _Optional[_Iterable[str]] = ...) -> None: ...
    SCORERS_FIELD_NUMBER: _ClassVar[int]
    SCORE_FIELD_NUMBER: _ClassVar[int]
    TEAM1_FIELD_NUMBER: _ClassVar[int]
    TEAM2_FIELD_NUMBER: _ClassVar[int]
    score: FootballGame.FootballScore
    scorers: FootballGame.Scorers
    team1: str
    team2: str
    def __init__(self, team1: _Optional[str] = ..., team2: _Optional[str] = ..., score: _Optional[_Union[FootballGame.FootballScore, _Mapping]] = ..., scorers: _Optional[_Union[FootballGame.Scorers, _Mapping]] = ...) -> None: ...

class GameScore(_message.Message):
    __slots__ = ["footballGame", "tennisGame"]
    FOOTBALLGAME_FIELD_NUMBER: _ClassVar[int]
    TENNISGAME_FIELD_NUMBER: _ClassVar[int]
    footballGame: FootballGame
    tennisGame: TennisGame
    def __init__(self, footballGame: _Optional[_Union[FootballGame, _Mapping]] = ..., tennisGame: _Optional[_Union[TennisGame, _Mapping]] = ...) -> None: ...

class GameSubscription(_message.Message):
    __slots__ = ["side1", "side2", "type"]
    class Type(int, metaclass=_enum_type_wrapper.EnumTypeWrapper):
        __slots__ = []
    FOOTBALL: GameSubscription.Type
    SIDE1_FIELD_NUMBER: _ClassVar[int]
    SIDE2_FIELD_NUMBER: _ClassVar[int]
    TENNIS: GameSubscription.Type
    TYPE_FIELD_NUMBER: _ClassVar[int]
    side1: str
    side2: str
    type: GameSubscription.Type
    def __init__(self, type: _Optional[_Union[GameSubscription.Type, str]] = ..., side1: _Optional[str] = ..., side2: _Optional[str] = ...) -> None: ...

class TennisGame(_message.Message):
    __slots__ = ["player1", "player2", "score"]
    class TennisScore(_message.Message):
        __slots__ = ["sets"]
        class GameScore(_message.Message):
            __slots__ = []
            class Points(int, metaclass=_enum_type_wrapper.EnumTypeWrapper):
                __slots__ = []
            ADV: TennisGame.TennisScore.GameScore.Points
            FIFTEEN: TennisGame.TennisScore.GameScore.Points
            FORTY: TennisGame.TennisScore.GameScore.Points
            THIRTY: TennisGame.TennisScore.GameScore.Points
            def __init__(self) -> None: ...
        class SetScore(_message.Message):
            __slots__ = ["player1Sets", "player2Sets"]
            PLAYER1SETS_FIELD_NUMBER: _ClassVar[int]
            PLAYER2SETS_FIELD_NUMBER: _ClassVar[int]
            player1Sets: int
            player2Sets: int
            def __init__(self, player1Sets: _Optional[int] = ..., player2Sets: _Optional[int] = ...) -> None: ...
        SETS_FIELD_NUMBER: _ClassVar[int]
        sets: _containers.RepeatedCompositeFieldContainer[TennisGame.TennisScore.SetScore]
        def __init__(self, sets: _Optional[_Iterable[_Union[TennisGame.TennisScore.SetScore, _Mapping]]] = ...) -> None: ...
    PLAYER1_FIELD_NUMBER: _ClassVar[int]
    PLAYER2_FIELD_NUMBER: _ClassVar[int]
    SCORE_FIELD_NUMBER: _ClassVar[int]
    player1: str
    player2: str
    score: _containers.RepeatedCompositeFieldContainer[TennisGame.TennisScore]
    def __init__(self, player1: _Optional[str] = ..., player2: _Optional[str] = ..., score: _Optional[_Iterable[_Union[TennisGame.TennisScore, _Mapping]]] = ...) -> None: ...
