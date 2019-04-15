;Gameboy DMG WLA-DX

.ROMDMG                         ;Pas de features CGB
.NAME "TESTSPRITE"              ;Nom du ROM inscrit dans le header
.CARTRIDGETYPE 0                ;ROM only
.RAMSIZE 0
.COMPUTEGBCHECKSUM              ;WLA-DX écrira le checksum lui-même (nécessaire sur une vraie GB)
.COMPUTEGBCOMPLEMENTCHECK       ;WLA-DX écrira le code de verif du header (nécessaire sur une vraie GB)
.LICENSEECODENEW "00"           ;Code de license Nintendo, j'en ai pas donc...
.EMPTYFILL $00                  ;Padding avec des 0

.MEMORYMAP
SLOTSIZE $4000
DEFAULTSLOT 0
SLOT 0 $0000
SLOT 1 $4000
.ENDME

.ROMBANKSIZE $4000              ;Deux banks de 16Ko
.ROMBANKS 2

.BANK 0 SLOT 0

.ENUM $C000
;add RAM variables here
SPRITE_X db
.ENDE

.ORG $0100
nop
jp    start                     ;Entry point

.ORG $0104
;Logo Nintendo, obligatoire
.db $CE,$ED,$66,$66,$CC,$0D,$00,$0B,$03,$73,$00,$83,$00,$0C
.db $00,$0D,$00,$08,$11,$1F,$88,$89,$00,$0E,$DC,$CC,$6E,$E6
.db $DD,$DD,$D9,$99,$BB,$BB,$67,$63,$6E,$0E,$EC,$CC,$DD,$DC
.db $99,$9F,$BB,$B9,$33,$3E

.org $0150
start:
  di

  xor a;
  ld (SPRITE_X),a

  ; wait for VBL before initializing VRAM and OAM
  call waitVBL

  ; as we are modifying VRAM, turn LCD off
;  ld a,$00
;  ldh ($40),a

  ; clear OAM
  ld hl,$FE00
  ld b,$50
  call clearMEM
  call waitVBL

  ld hl,$FE50
  ld b,$50
  call clearMEM
  call waitVBL
  
;  ld hl,$FE40
;  ld b,$20
;  call clearMEM
;  call waitVBL

;  ld hl,$FE60
;  ld b,$20
;  call clearMEM
;  call waitVBL
  
;  ld hl,$FE80
;  ld b,$20
;  call clearMEM
;  call waitVBL

  ; copy a single tile to VRAM $8010 (tile 25)
  ld b,16
  ld hl,tiles
  ld de,$8000+26*$10
copyTile:
  ld a,(hl+)
  ld (de),a
  inc de
  dec b
  jp nz,copyTile

  ; Base address of SPRITE[0]
  ld hl,$FE00
  ; SPRITE[0].y = 16
  ld a,16
  ld (hl),a
  inc l
  ; SPRITE[0].x = 8
  ld a,(SPRITE_X)
  ld (hl),a
  inc l
  ; SPRITE[0].tile = 26
  ld a,26
  ld (hl),a
  inc l
  ; SPRITE[0].flags = NO_FLIP | PAL0
  ld (hl),a

  ld a,$93            ; BG on, tiles at $8000
  ldh ($40),a

  ld b,%11100100 ; palette for sprites
  ld c,$04 ; palette rotation counter

loop:
  ld a,(SPRITE_X)
  inc a
  cp 168
  jr c,noOffscreenX
  xor a
noOffscreenX:
  ld (SPRITE_X),a
  call waitVBL
  dec c
  jr nz,+
  ld a,b  ; reload sprite palette
  rlca     ; rotate palette
  rlca     ; rotate palette
  ld hl,$FF48
  ld (hl+),a ; load sprite palette 0
  ld (hl+),a ; load sprite palette 1
  ld b,a
  ld c,$04  ; reset palette counter
+ ld a,(SPRITE_X)
  ld ($FE01),a
  jp loop     

waitVBL:
    ; wait for VBL (LY=144)
  ldh a,($44)
  cp 144 
  jr c,waitLY144
waitLY0:   
  ldh a,($44)
  cp 0 
  jr nz,waitLY0
waitLY144:  
  ldh a,($44)
  cp  144 
  jp  c,waitLY144
  ret

clearMEM:
  xor a
  ld (hl),a
  inc l
  dec b
  jr nz,clearMEM
  ret

.org $0800
tiles:
.db $FF,$FF,$81,$81,$81,$81,$81,$81,$81,$81,$81,$81,$81,$81,$FF,$FF
;  .INCBIN "tiles.bin"
