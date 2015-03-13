# Introduction #

Drag-and-drop, animation, local storage and sound effects!


# Details #

Drag-and-drop: now player may use drag-n-drop in phase 2 & 3 to move pieces: [Graphics.java](https://code.google.com/p/fainty-nmm/source/browse/src/com/google/gwt/faintynmm/client/ui/Graphics.java), [Presenter.java](https://code.google.com/p/fainty-nmm/source/browse/src/com/google/gwt/faintynmm/client/ui/Presenter.java).

Animation: added two types of animation: [PieceColorAnimation.java](https://code.google.com/p/fainty-nmm/source/browse/src/com/google/gwt/faintynmm/client/ui/PieceColorAnimation.java), [InfoUpdateAnimation.java](https://code.google.com/p/fainty-nmm/source/browse/src/com/google/gwt/faintynmm/client/ui/InfoUpdateAnimation.java).

Local storage: use for saving and loading. Besides, if no history token is given in URL, will then try to load game from local storage automatically. See [Presenter.java](https://code.google.com/p/fainty-nmm/source/browse/src/com/google/gwt/faintynmm/client/ui/Presenter.java).

Sound effects: add SFX for placing, moving and removing pieces. See [Presenter.java](https://code.google.com/p/fainty-nmm/source/browse/src/com/google/gwt/faintynmm/client/ui/Presenter.java).