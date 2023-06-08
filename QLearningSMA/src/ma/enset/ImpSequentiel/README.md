# Sequentiel

Exemple: 

|  0  |   0   | F=1 |
|:---:|:-----:|:---:|
|  0  | OB=-1 |  0  |
|  A  |   0   |  0  |

- Agent va deplacer d'un etat vers l'autre 
- Les actions : decplacer a gauche / a droite / vers le haut /vers le bas.
- les recompances : 
  - si arrive a le point finale je le donne 1.
  - si arrive a l'obstacle je donne -1.
  - si deplace je donne 0.
- Q- table (table d'apprentisage):

| taches | gauche | droite | bas   | haut |
|--------|--------|--------|-------|------|
| 1      | 0      | 0      | 0     | 0    |
| 2      | 0      | 0      | 0     | 0    |
| 3      | 0      | 0      | 0     | 0    |
| 4      | 0      | 0      | 0     | 0    |
| 5      | 0      | 0      | 0     | 0    |
| 6      | 0      | 0      | 0     | 0    |
| 7      | -0.1   | 0.7    | - 0.2 | 0.05 |
| 8      | 0      | 0      | 0     | 0    |
| 9      | 0      | 0      | 0     | 0    |

il va choisir l'action qui a la valeur maximale.
l'agent va deplacer vers l'etape 8, va apprendre et choisir l'action qu'a la valeur maximale.
Pour modifier 
Les parametres de belman :
α=0.1
Y=0.9
e=0.3




