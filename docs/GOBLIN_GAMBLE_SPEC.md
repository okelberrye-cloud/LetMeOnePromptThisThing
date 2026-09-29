# Goblin Gamble: Final Game Spec (Lab 5, Part 4 Extra Credit)

A higher-or-lower card game where a grumpy card goblin guards a vault of 13 cards, and every suit does something chaotic. Built on top of the Part 1 card display code (same `Card`, `Deck`, and image loading classes).

## Initial Setup

- Shuffle the 52-card deck (same shuffle as Part 1).
- Deal 13 cards face down in a row. This is **the Vault**.
- Flip one extra card face up. This is **the current card**.
- The player starts with **3 lives** (shown as hearts, max 5), **0 points**, and the **Goblin Anger Meter at 0/5**.
- A goblin sits in the middle of the table with a speech bubble and taunts the player.

## Main Game Loop

1. The player picks **Higher** or **Lower** for the next Vault card (keyboard: Up / Down).
2. The next Vault card flips over, then becomes the new current card.
3. The guess is scored: correct is **+10 points**, wrong is **-1 life**.
4. The revealed card's **suit power** fires.
5. The revealed card's **face card power** fires (J, Q, K only).
6. If the goblin is angry enough, he throws a **tantrum** and shuffles the rest of the Vault.
7. If a Queen was revealed, the player peeks at the next Vault card.
8. The game checks for a win or loss. If neither, go back to step 1.

## Game Rules

**Comparing cards**
- Aces are high (2 is lowest, Ace is 14).
- A tie counts as a correct guess.

**Suit powers** (always fire, even on a wrong guess)
| Suit | Power |
|---|---|
| ♥ Hearts | +1 life (max 5) |
| ♠ Spades | Double the points from this guess (a correct Spade is +20) |
| ♦ Diamonds | +25 bonus coins (coins are added to the score) |
| ♣ Clubs | +1 Goblin Anger |

**Face card powers**
| Card | Power |
|---|---|
| Jack | Your next flip is free. The Higher/Lower buttons are replaced with a Free Flip button: no points, no life lost, but the flipped card's suit and face powers still fire. |
| Queen | Peek at the next Vault card. It's shown face up in the Vault until you guess. |
| King | The goblin steals 30 points (score can't go below 0). |

**Goblin tantrum**
- Whenever a Club pushes anger to **3 or 4**, the goblin spins, the Vault cards shake, and the remaining Vault is shuffled.
- The Queen's peek happens after the tantrum, so a peek is always accurate.

## Success State

The Vault is empty and the player has at least 1 life. The goblin cries into his crown and a **"VAULT CRACKED!"** screen shows the final score with a Play Again button.

## Failure State

Either of these ends the game with a **"GOBLIN WINS"** screen, the reason, the number of cards cracked, the final score, and Play Again:
- **Lives hit 0.**
- **Goblin Anger hits 5/5.** The goblin eats the deck.

If both happen on the same card, running out of lives is the reason shown.

---

## Spec Clarifications (questions asked during the build)

The lab asks the LLM to ask about gaps instead of filling them in. These were the questions and answers:

| Question | Answer |
|---|---|
| When a wrong guess reveals a Heart, does the Heart still give a life back? | **Yes. Suit powers always fire.** A wrong Heart is a net 0 lives. |
| Where should the card art come from? | **A CC0 pack from OpenGameArt** (Byron Knoll's Vector Playing Cards). |
| Is there existing Part 1 code to build on? | **No.** Part 1 was built in the same project. |

These smaller calls were made during the build and are easy to change in `GoblinGame.java` if the team wants something different:

| Decision | Where to change it |
|---|---|
| "Coins" from Diamonds are added straight to the score | `DIAMOND_BONUS` |
| Tantrum fires at anger 3 **and** 4 (not only the first time) | `TANTRUM_ANGER` check in `play()` |
| A Jack revealed as the last Vault card gives no free pass (nothing left to flip) | Jack branch in `play()` |
| King steal can't take the score below 0 | King branch in `play()` |

## Prompt Used

> This is student work for a software engineering lab. Here is our spec for an interactive card game (Initial Setup, Main Game Loop, Game Rules, Success State, Failure State) and our card display code from Part 1. Please build the game in JavaFX on top of that code. Instead of filling in gaps, ask me questions about anything that is missing or unclear in the spec before writing code.
