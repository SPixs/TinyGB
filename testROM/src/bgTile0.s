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
  ; wait for VBL before initializing VRAM and OAM
  ldh a,($44)
  cp 144  
  jp c,start

  ; copy a single tile to VRAM $8000
  ld b,16
  ld hl,tiles
  ld de,$8000
copyTile:
  ld a,(hl+)
  ld (de),a
  inc de
  dec b
  jp nz,copyTile

  ; write tile 0 to BG Map at 
  xor a
  ld hl,$9800
  ld (hl),a

  jp start     


.org $0800
tiles:
.db $FF,$FF,$81,$81,$81,$81,$81,$81,$81,$81,$81,$81,$81,$81,$FF,$FF
;  .INCBIN "tiles.bin"
