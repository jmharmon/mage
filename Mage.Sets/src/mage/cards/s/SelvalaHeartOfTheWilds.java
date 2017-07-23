/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.s;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldAllTriggeredAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.GreatestPowerAmongControlledCreaturesValue;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.AddManaInAnyCombinationEffect;
import mage.abilities.effects.common.ManaEffect;
import mage.abilities.mana.SimpleManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.Predicate;
import mage.filter.predicate.permanent.AnotherPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;

/**
 *
 * @author maxlebedev
 */
public class SelvalaHeartOfTheWilds extends CardImpl {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("another creature");

    static {
        filter.add(new AnotherPredicate());
    }

    private static final String rule = "Whenever another creature enters the battlefield, its controller may draw a card if its power is greater than each other creature's power.";
    private static final String rule2 = "Add X mana in any combination of colors to your mana pool, where X is the greatest power among creatures you control.";

    public SelvalaHeartOfTheWilds(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{G}{G}");
        addSuperType(SuperType.LEGENDARY);
        this.subtype.add("Elf");
        this.subtype.add("Scout");
        this.power = new MageInt(2);
        this.toughness = new MageInt(3);

        // Whenever another creature enters the battlefield, its controller may draw a card if its power is greater than each other creature's power.
        this.addAbility(new EntersBattlefieldAllTriggeredAbility(Zone.BATTLEFIELD, new SelvalaHeartOfTheWildsEffect(), filter, false, SetTargetPointer.PERMANENT, rule));

        // {G}, {T}: Add X mana in any combination of colors to your mana pool, where X is the greatest power among creatures you control.
        ManaEffect manaEffect = new AddManaInAnyCombinationEffect(new GreatestPowerAmongControlledCreaturesValue(), rule2, ColoredManaSymbol.B, ColoredManaSymbol.U, ColoredManaSymbol.R, ColoredManaSymbol.W, ColoredManaSymbol.G);
        Ability ability = new SimpleManaAbility(Zone.BATTLEFIELD, manaEffect, new ManaCostsImpl("{G}"));
        ability.addCost(new TapSourceCost());
        this.addAbility(ability);

    }

    public SelvalaHeartOfTheWilds(final SelvalaHeartOfTheWilds card) {
        super(card);
    }

    @Override
    public SelvalaHeartOfTheWilds copy() {
        return new SelvalaHeartOfTheWilds(this);
    }
}

class SelvalaHeartOfTheWildsEffect extends OneShotEffect {

    private static final FilterCreaturePermanent filter2 = new FilterCreaturePermanent();

    static {
        filter2.add(new GreatestPowerPredicate());
    }

    public SelvalaHeartOfTheWildsEffect() {
        super(Outcome.Benefit);
        this.staticText = "that creature's controller may draw a card";
    }

    public SelvalaHeartOfTheWildsEffect(final SelvalaHeartOfTheWildsEffect effect) {
        super(effect);
    }

    @Override
    public SelvalaHeartOfTheWildsEffect copy() {
        return new SelvalaHeartOfTheWildsEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanentOrLKIBattlefield(targetPointer.getFirst(game, source));
        if (permanent != null) {
            if (filter2.match(permanent, game)) {
                Player permanentController = game.getPlayer(permanent.getControllerId());
                if (permanentController != null
                        && permanentController.chooseUse(Outcome.DrawCard, "Would you like to draw a card?", source, game)) {
                    permanentController.drawCards(1, game);
                }
            }
            return true;
        }
        return false;
    }
}

class GreatestPowerPredicate implements Predicate<Permanent> {

    @Override
    public boolean apply(Permanent input, Game game) {
        int power = input.getPower().getValue();
        for (UUID playerId : game.getPlayerList()) {
            Player player = game.getPlayer(playerId);
            if (player != null) {
                for (Permanent permanent : game.getBattlefield().getActivePermanents(new FilterCreaturePermanent(), playerId, game)) {
                    if (permanent.getPower().getValue() >= power && !permanent.equals(input)) {
                        return false; //we found something with equal/more power
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "Greatest Power";
    }
}

class GreatestPowerYouControlValue implements DynamicValue {

    @Override
    public int calculate(Game game, Ability sourceAbility, Effect effect) {
        Player player = game.getPlayer(sourceAbility.getControllerId());
        int amount = 0;
        if (player != null) {
            for (Permanent permanent : game.getBattlefield().getActivePermanents(new FilterControlledCreaturePermanent(), sourceAbility.getControllerId(), game)) {
                if (permanent.getPower().getValue() > amount) {
                    amount = permanent.getPower().getValue();
                }
            }
        }
        return amount;
    }

    @Override
    public DynamicValue copy() {
        return new GreatestPowerYouControlValue();
    }

    @Override
    public String getMessage() {
        return "Add X mana in any combination of colors to your mana pool, where X is the greatest power among creatures you control.";
    }
}
