TEST    START   1000
        LDA     ALPHA
        ADD     BETA
        JSUB    SUBR
        COMP    GAMMA
        JEQ     END_LABEL
        LDA     GAMMA
        ADD     BETA
ALPHA   WORD    1234
BETA    WORD    5678
GAMMA   WORD    9999
SUBR    RESW    1
END_LABEL RSUB
        END     TEST
