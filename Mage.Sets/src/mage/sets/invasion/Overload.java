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
package mage.sets.invasion;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.condition.common.KickedCondition;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.abilities.keyword.KickerAbility;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.filter.Filter;
import mage.filter.common.FilterArtifactPermanent;
import mage.filter.predicate.mageobject.ConvertedManaCostPredicate;
import mage.game.Game;
import mage.target.common.TargetArtifactPermanent;

/**
 *
 * @author LoneFox
 */
public class Overload extends CardImpl {

    private static final FilterArtifactPermanent filter2 = new FilterArtifactPermanent("artifact if its converted mana cost is 2 or less");
    private static final FilterArtifactPermanent filter5 = new FilterArtifactPermanent("artifact if its converted mana cost is 5 or less");

    static {
        filter2.add(new ConvertedManaCostPredicate(Filter.ComparisonType.LessThan, 3));
        filter5.add(new ConvertedManaCostPredicate(Filter.ComparisonType.LessThan, 5));
    }

    public Overload(UUID ownerId) {
        super(ownerId, 157, "Overload", Rarity.COMMON, new CardType[]{CardType.INSTANT}, "{R}");
        this.expansionSetCode = "INV";

        // Kicker {2}
        this.addAbility(new KickerAbility("{2}"));
        // Destroy target artifact if its converted mana cost is 2 or less. If Overload was kicked, destroy that artifact if its converted mana cost is 5 or less instead.
        this.getSpellAbility().addEffect(new DestroyTargetEffect());
        this.getSpellAbility().addTarget(new TargetArtifactPermanent(filter5));
    }

    @Override
    public void adjustTargets(Ability ability, Game game) {
        if(ability instanceof SpellAbility) {
            if(!KickedCondition.getInstance().apply(game, ability)) {
                ability.getTargets().clear();
                ability.getTargets().add(new TargetArtifactPermanent(filter2));
            }
        }
    }

    public Overload(final Overload card) {
        super(card);
    }

    @Override
    public Overload copy() {
        return new Overload(this);
    }
}
