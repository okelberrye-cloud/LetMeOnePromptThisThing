# Part 1: Card Display, Story Beats and Estimates

**User story:** Prof Horton wants an App that will display all 52 cards in a standard card deck and shuffle the cards upon button press.

**Cost scale:** 1, 2, 3, 5, 8, 12, 20

## Story Beats

| # | Story beat | Estimate | Actual (edit) |
|---|---|---|---|
| 1 | Set up a JavaFX project (Maven) that opens a window | 2 | 2 |
| 2 | Find CC0 card images and add them to the project | 2 | 3 |
| 3 | Model the deck: `Suit`, `Rank`, `Card`, `Deck` with all 52 cards | 3 | 2 |
| 4 | Load card images and show all 52 in a 13 x 4 grid on a green table | 5 | 5 |
| 5 | Shuffle button reshuffles the deck and redraws the grid | 2 | 1 |
| 6 | Polish: shuffle animation, Sort button, shuffle counter | 3 | 3 |
| 7 | Test it and record the demo video | 2 | 2 |
| | **Total** | **19** | **18** |

## Reflection (draft: edit to match your team's experience)

Our estimates were close overall (19 estimated vs 18 actual), but they were off in different places than we expected.

- **Finding the card art took longer than planned.** Most packs we found had unclear licenses. The pack we used (Byron Knoll's Vector Playing Cards on OpenGameArt) is CC0, but it has no card back, so we had to make our own, and we swapped out the fancy Ace of Spades because its license was questionable.
- **The shuffle itself was easier than planned.** `Collections.shuffle` does the real work, so beat 5 was basically wiring a button to two method calls.
- **Displaying the cards was the right size estimate.** Getting 52 images to scale evenly in a grid that fits a laptop screen took some trial and error with the card width and gaps.
- **Lesson for next time:** asset hunting and licensing is real work and should get its own bigger estimate, and anything that's a one-line library call can be estimated lower.
