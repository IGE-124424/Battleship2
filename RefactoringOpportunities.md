| Local                           | Code Smell | Refactoring | Nº aluno |
|---------------------------------|-------------|-------------|---------|
| Game::getBoard (linha 435)      | Cognitive Complexity (18→15) | Extract Method | 110420 |
| Game::getAlienFleet (linha 188) | Inconsistent Getter | Correct Getter Refactoring | 110420 |
| Game::randomEnemyFire (linha 208) | Cognitive Complexity (18>15) | Extract Method | 110420 |
| Fleet::createRandom (linha 30) | Duplicate Literal       | Extract Constant ("caravela")  | 110420  |
| Fleet::createRandom (linha 31) | Duplicate Literal       | Extract Constant ("barca")     | 110420  |
| Move::processEnemyFire (linha 106)| Duplicate Literal     | Extract Constant ("tiro")        | 110420 |
| Ship::tooCloseTo (linha 352)  | Conditional without Braces   | Add Braces                      | 110420 |