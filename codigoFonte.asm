TEST    START   1000
        LDA     ALPHA
        ADD     BETA
        JSUB    SUBR
        COMP    GAMMA
        JEQ     END
        LDA     GAMMA
        ADD     BETA
ALPHA   WORD    1234
BETA    WORD    5678
GAMMA   WORD    9ABC
SUBR    RESW    1
        END     TEST