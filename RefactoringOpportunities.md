| Local                           | Code Smell | Refactoring | Nº aluno |
|---------------------------------|-------------|-------------|---------|
| Game::getBoard (linha 435)      | Cognitive Complexity (18→15) | Extract Method | 110420 |
| Game::getAlienFleet (linha 188) | Inconsistent Getter | Correct Getter Refactoring | 110420 |
| Game::randomEnemyFire (linha 208) | Cognitive Complexity (18>15) | Extract Method | 110420 |
| Fleet::createRandom (linha 30) | Duplicate Literal | Extract Constant ("caravela") | 110420 |
| Fleet::createRandom (linha 31) | Duplicate Literal | Extract Constant ("barca") | 110420 |
| Move::processEnemyFire (linha 106) | Duplicate Literal | Extract Constant ("tiro") | 110420 |
| Ship::tooCloseTo (linha 352) | Conditional without Braces | Add Braces | 110420 |
| Tasks::gerarPdf | Poor Naming | Rename Method | 122486 |
| Tasks::menu | Long Method | Extract Method (printScoreboard) | 122486 |
| Tasks::menu | Long Method | Extract Method (handleStatus) | 122486 |
| Tasks::menu | Duplicate Logic | Parameterize Method (handleMap) | 122486 |
| PdfReportGenerator::generateMovesReport | Long Method | Extract Method (addReportHeader) | 122486 |
| PdfReportGenerator::extractCoordinates | Duplicate Initialization | Introduce Constant | 122486 |
| Ship::buildShip | Poor Naming | Rename Variable | 122486 |
| Ship::buildShip | Complex Switch | Simplify Control Flow | 122486 |
| Position::adjacentPositions | Magic Array | Extract Constant (ADJACENT_DIRECTIONS) | 124424 |
| Position::randomPosition | Magic Number Usage | Extract Variable (boardSize) | 124424 |
| Move::toString | Long Expression | Extract Variable (shotCount, resultCount) | 124424 |
| Move::processEnemyFire | Long Method | Extract Method (printVerboseResult) | 124424 |
| Move::processEnemyFire | Long Method | Extract Method (buildResponseMap) | 124424 |
| Move::processEnemyFire | Duplicate Map Construction | Extract Method (buildSunkBoatsList, buildBoatHitsList) | 124424 |
| Game::jsonShots | Temporary Variable | Inline Variable | 124424 |
| Game::fireShots | Long Method | Extract Method (validateShotCount) | 124424 |
| Game::readEnemyFire | Long Method | Extract Method (addShotFromToken, addSeparatedClassicPosition) | 124424 |
| Ship::Ship | Redundant Assignment | Remove Redundant Assignment | 123779 |
| Ship::tooCloseTo(IShip) | Iterator Usage | Replace Iterator with Enhanced For | 123779 |
| Ship::toString | Long String Concatenation | Introduce Variable | 123779 |
| BoardGUI::start | Possible Null Resource Stream | Extract Method (loadShipImage) | 123779 |
| BoardGUI::start | Long Method / Mixed Responsibilities | Extract Method (createBoardGrid) | 123779 |
| BoardGUI::start | Hardcoded Literal | Extract Constant (WINDOW_TITLE) | 123779 |