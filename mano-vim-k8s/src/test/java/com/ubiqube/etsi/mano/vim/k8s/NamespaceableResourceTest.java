/**
 *     Copyright (C) 2019-2024 Ubiqube.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.ubiqube.etsi.mano.vim.k8s;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import io.fabric8.kubernetes.api.builder.Visitor;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.ListOptions;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.api.model.autoscaling.v1.Scale;
import io.fabric8.kubernetes.client.GracePeriodConfigurable;
import io.fabric8.kubernetes.client.PropagationPolicyConfigurable;
import io.fabric8.kubernetes.client.ResourceNotFoundException;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.dsl.Deletable;
import io.fabric8.kubernetes.client.dsl.EditReplacePatchable;
import io.fabric8.kubernetes.client.dsl.Gettable;
import io.fabric8.kubernetes.client.dsl.Informable;
import io.fabric8.kubernetes.client.dsl.NamespaceableResource;
import io.fabric8.kubernetes.client.dsl.NonDeletingOperation;
import io.fabric8.kubernetes.client.dsl.ReplaceDeletable;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.ServerSideApplicable;
import io.fabric8.kubernetes.client.dsl.Watchable;
import io.fabric8.kubernetes.client.dsl.WritableOperation;
import io.fabric8.kubernetes.client.dsl.base.PatchContext;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;

public class NamespaceableResourceTest<T> implements NamespaceableResource<T> {

	private final T arg;

	public NamespaceableResourceTest(final T argument) {
		this.arg = argument;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T require() throws ResourceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReplaceDeletable<T> lockResourceVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReplaceDeletable<T> lockResourceVersion(final String resourceVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T item() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T scale(final int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T scale(final int count, final boolean wait) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Scale scale(final Scale scale) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Gettable<T> fromServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T get() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Watchable<T> withResourceVersion(final String resourceVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Watch watch(final Watcher<T> watcher) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Watch watch(final ListOptions options, final Watcher<T> watcher) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Watch watch(final String resourceVersion, final Watcher<T> watcher) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T waitUntilReady(final long amount, final TimeUnit timeUnit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T waitUntilCondition(final Predicate<T> condition, final long amount, final TimeUnit timeUnit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T replaceStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T updateStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T replace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T update() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StatusDetails> delete() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Deletable withTimeout(final long timeout, final TimeUnit unit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Deletable withTimeoutInMillis(final long timeoutInMillis) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T replaceStatus(final T item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T replace(final T item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T createOr(final Function<NonDeletingOperation<T>, T> conflictAction) {
		return arg;
	}

	@Override
	public NonDeletingOperation<T> unlock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T editStatus(final UnaryOperator<T> function) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T patchStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EditReplacePatchable<T> subresource(final String subresource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T createOrReplace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T create() {
		return arg;
	}

	@Override
	public T edit(final UnaryOperator<T> function) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T edit(final Visitor... visitors) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> T edit(final Class<V> visitorType, final Visitor<V> visitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T accept(final Consumer<T> function) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T patch(final PatchContext patchContext, final T item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T patch(final PatchContext patchContext, final String patch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T patch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T patch(final PatchContext patchContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T createOrReplace(final T item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T create(final T item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StatusDetails> delete(final T item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T updateStatus(final T item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T patchStatus(final T item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropagationPolicyConfigurable<? extends Deletable> withGracePeriod(final long gracePeriodSeconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GracePeriodConfigurable<? extends Deletable> withPropagationPolicy(final DeletionPropagation propagationPolicy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T serverSideApply() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerSideApplicable<T> fieldManager(final String manager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerSideApplicable<T> forceConflicts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NonDeletingOperation<T> fieldValidation(final Validation fieldValidation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WritableOperation<T> dryRun() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WritableOperation<T> dryRun(final boolean isDryRun) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Informable<T> withIndexers(final Map<String, Function<T, List<String>>> indexers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Informable<T> withLimit(final Long limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SharedIndexInformer<T> inform(final ResourceEventHandler<? super T> handler, final long resync) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SharedIndexInformer<T> runnableInformer(final long resync) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<List<T>> informOnCondition(final Predicate<List<T>> condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource<T> inNamespace(final String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
